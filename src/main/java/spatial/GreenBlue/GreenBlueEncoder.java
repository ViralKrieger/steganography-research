package spatial.GreenBlue;

import lib.BitIterator;

import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import static java.nio.charset.StandardCharsets.*;

/*
 This encoding scheme embeds the message within the 2nd to 8th bit in the blue or green
 channel (or both) of a pixel. The embedded bit position is chosen with a hash function. This makes
 the bit selection behave randomly and allows the decoder to select the same randomly selected bits.
 */
public class GreenBlueEncoder {

    private String imageFileName;
    private Optional<BufferedImage> stegoImage = Optional.empty();

    public static void encode(String inputImg, String outputImg, String message) {
        /*
        Step 1
        Select Secret Key Sk
        Convert message into binary stream M
        */

        /*
        Step 2
        Scramble original message. Replace 1st bit with 8th bit, 2nd with 7th, 3rd with 6th,
        and 4th with 5th. This gives us Mm.
         */
        BitIterator bitMessage;
        try {
            bitMessage = new BitIterator(message);
        } catch (UnsupportedEncodingException e){
            throw new RuntimeException("Could not encode message: " + e.getMessage());
        }
        GreenBlueEncoder.scrambleMessage(bitMessage);

        /*
        Step 3
        Calculate R0, the number of 0's in the Red channel of the current pixel.
        Calculate R1 - Sk - R0
         */

        /*
        Step 4
        Calculate position of bit in Blue channel Pb = ((Iz + R1) % 7) + 1
         */

        /*
        Step 5

        if bit value of Pb position of the current pixel in the Blue channel and current
        bit of Mm are equal,
            LSB position of Blue component is set to 0
        else
            LSB position of Blue component is set to 1
         */

        /*
        Step 6
        Repeat steps 3 to 5 until Mm is finished or Blue component is finished
         */

        /*
        Step 7
        if Blue component is finished but Mm still remains,
            Select the starting pixel of cover image
         */

        /*
        Step 8
        Calculate R2, the number of 1s in the Red channel of the current pixel.
        Calculate R3 = Sk - R2
         */

        /*
        Step 9
        Calculate the position of bit in Green channel Pg = ((Iz + R3) % 7) + 1
         */

        /*
        Step 10
        if bit value of Pg position of the current pixel in the Green channel and current
        bit of Mm are equal,
            LSB position of Green component is set to 0
        else
            LSB position of Green component is set to 0
         */

        /*
        Step 11
        Repeat steps 8 to 10 until the bit stream is finished
         */
    }

    private static void scrambleMessage(BitIterator bitMessage) {
        ArrayList<Byte> bitMessageScrambled = new ArrayList<>();

        while (bitMessage.hasNext()) {

            ArrayList<Byte> bitList = new ArrayList<>();
            for (int bitCount = 0; bitCount < 8; bitCount++) {
                if (bitMessage.hasNext()) {
                    // BitIterator's next method returns bits as bytes, so currentBit is a byte
                    // with the value of either 0 or 1
                    bitList.add(bitMessage.next());
                } else {
                    break;
                }
            }
            // Swaps bits in positions 1 and 8, 2 and 7, 3 and 6, and 4 and 5
            // If do not have a set of 8 bits to swap, do not swap
            if (bitList.size() == 8) {
                Collections.swap(bitList, 0, 7);
                Collections.swap(bitList, 1, 6);
                Collections.swap(bitList, 2, 5);
                Collections.swap(bitList, 3, 4);
            }

            for (int bitShift = 0; bitShift < bitList.size(); bitShift++) {
                
            }


            //


        }
    }


    public String decode() {
        return "";
        /*
        Step 1
        if LSB of Blue component of the current pixel is 0,
            Calculate R0, number of 0's in Red channel of current pixel
            Calculate Rz = Sk - R0
            Calculate Pb = ((Iz + R1) % 7) + 1
            Select the bit at position Pb and store it into the bit stream
        else
            Go to next pixel
         */

        /*
        Step 2
        Repeat step 1 until the bit stream is finished or the blue component is finished
         */

        /*
        Step 3
        if Blue component is finished, but bit stream still remains in image,
            Select the starting pixel of the cover image
         */

        /*
        Step 4
        if the LSB of Green component of the current pixel is 0,
            Calculate R2, number of 1's in Red channel of the current pixel
            Calculate R3 = Sk - R2
         */

        /*
        Step 5
        Repeat the previous step until the bit stream is finished
         */

        /*
        Step 6
        Unscramble the message. Replace 1st bit with 8th bit, 2nd with 7th, 3rd with 6th,
        and 4th with 5th. This gives us Mm.
         */
    }
}
