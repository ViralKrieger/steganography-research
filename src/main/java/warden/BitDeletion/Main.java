package warden.BitDeletion;

import org.apache.commons.cli.*;
import warden.BitDeletion.BitDeleter;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        Options options = CreateCmdLineOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Bit deletion warden", options);

            System.exit(1);
            return;
        }

        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");

        try {
            RunWarden(inputFilePath, outputFilePath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static Options CreateCmdLineOptions() {
        Options options = new Options();

        Option input = new Option("i", "input", true, "input image path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output image path");
        output.setRequired(false);
        options.addOption(output);

        return options;
    }

    private static void RunWarden(String inputFilePath, String outputFilePath) throws IOException {
        BitDeleter warden = new BitDeleter(inputFilePath);
        warden.ScrubImage();
        warden.WriteImage(outputFilePath);
    }
}
