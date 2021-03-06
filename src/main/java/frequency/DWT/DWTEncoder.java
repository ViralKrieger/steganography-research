package frequency.DWT;

import lib.BitBuilder;
import lib.BitIterator;
import lib.Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Impl based off of Kamila, Roy, and Changder, "A DWT based Steganography Scheme with Image Block Partitioning"
 * Uses a 2D Haar Wavelet transform to encode message bits
 */

public class DWTEncoder implements Encoder {
    private String imageFileName;
    private Optional<BufferedImage> stegoImage = Optional.empty();
    private List<Integer> colorComponentMasks = new ArrayList<>(Arrays.asList(0x000000FF, 0x0000FF00, 0x00FF0000));

    public DWTEncoder(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public double GetCapacityFactor() {
        return 3/32;
    }

    public String GetName() {
        return "DWT";
    }

    public void SetImage(String path) {
        this.imageFileName = path;
    }


    public boolean WriteImage(String outputFileName) throws IOException {
        if(this.stegoImage.isPresent() == false) {
            return false;
        }

        File outputImageFile = new File(outputFileName);
        ImageIO.write(this.stegoImage.get(), "png", outputImageFile);

        return true;
    }

    /**
     * Encodes the hidden message using Case 3 for now
     * We are handling the encryption separately, so we do not need to worry about the Cantor Encryption Strategy
     * @param message
     * @throws IOException
     */
    public void Encode(String message) throws IOException {
        message += BitIterator.END_DELIMITER;
        BitIterator B = new BitIterator(message);

        BufferedImage C = ImageIO.read(new File(this.imageFileName));
        int width = C.getWidth();
        int height = C.getHeight();

        encodingLoop:
        for (int idx = 0; idx < colorComponentMasks.size(); idx++) {
            //Embed all of the message data into the transformed image blocks for this component
            Integer mask = colorComponentMasks.get(idx);
            int[][] coefficients = forwardHaar(C, mask, idx*8);

            for (int j = height / 2; j < height; j++) {
                for (int i = width / 2; i < width; i++) {
                    if (B.hasNext() == false) {
                        C = reverseHaar(C, coefficients, mask);
                        break encodingLoop;
                    }

                    int nextByte = B.next();
                    int coefficientParody = coefficients[i][j] & 0x00000001;

                    if (coefficientParody == 1 && nextByte == 0) {
                        //effectively adding two instead of subtracting one
                        //done to preserve value through haar inverse
                        coefficients[i][j] += 1;
                    } else if (coefficientParody == 0 && nextByte == 1) {
                        coefficients[i][j] += 1;
                    }
                }
            }

            C = reverseHaar(C, coefficients, mask);
        }

        //Store the inverted image
        //Inverted image is the final stego image with the message embedded in the DWT values but not the final RGB
        this.stegoImage = Optional.of(C);
    }

    private int[][] forwardHaar(BufferedImage C, Integer mask, int shiftAmt) {
        int imageHeight = C.getHeight();
        int imageWidth = C.getWidth();

        int[][] pixelData = new int[imageWidth][imageHeight];

        //get the pixel data to transform with the color component from the mask
        for (int row = 0; row < imageWidth; row++) {
            for (int col = 0; col < imageHeight; col++) {
                pixelData[row][col] = (C.getRGB(row, col) & mask) >> shiftAmt;
            }
        }

        return HaarTransformer.forward(pixelData);
    }


    private BufferedImage reverseHaar(BufferedImage C, int[][] coefficients, Integer mask) {
        //apply the haar function
        int[][] pixelData = HaarTransformer.reverse(coefficients);

        for (int row = 0; row < C.getHeight(); row++) {
            for (int col = 0; col < C.getWidth(); col++) {
                int oldColor = C.getRGB(col, row);
                int newColor = 0xFF000000 + (pixelData[col][row] << 16) + (pixelData[col][row] << 8) + (pixelData[col][row]);
                newColor = (newColor & (0xFF000000 | mask)) | (oldColor & ~(0xFF000000 | mask));
                C.setRGB(col, row, newColor);
            }
        }

        return C;
    }

    //TODO needs updated
//    private BufferedImage visualizeHaar(BufferedImage C, int[][] coeffs, Integer mask) {
//        double max = coeffs[0][0];
//        for(int i = 0; i < coeffs.length; i++) {
//            for(int j = 0; j < coeffs[0].length; j++) {
//                if(max < coeffs[i][j]) {
//                    max = coeffs[i][j];
//                }
//            }
//        }
//
//        for (int row = 0; row < coeffs.length; row++) {
//            for (int col = 0; col < coeffs[0].length; col++) {
//                double val = 255 * (coeffs[col][row] / max);
//                int newColor = mask + ((int) val << 16) + ((int) val << 8) + ((int) val);
//                C.setRGB(col, row, newColor);
//            }
//        }
//
//        return C;
//    }

    public String Decode() throws IOException {
        BufferedImage S = ImageIO.read(new File(this.imageFileName));
        return DecodeFromImage(S);
    }

    private String DecodeFromImage(BufferedImage S) throws IOException {
        BitBuilder result = new BitBuilder();
        int b = 0x00;

        int width = S.getWidth();
        int height = S.getHeight();

        encodingLoop:
        for (int idx = 0; idx < colorComponentMasks.size(); idx++) {
            Integer mask = colorComponentMasks.get(idx);
            int[][] coefficients = forwardHaar(S, mask, idx*8);

            for (int j = height / 2; j < height; j++) {
                for (int i = width / 2; i < width; i++) {
                    b = (coefficients[i][j] & 0x00000001);

                    if (result.append((byte) b)) break encodingLoop;
                }
            }
        }

        //return final string formed from bytes
        return result.toString();
    }
}