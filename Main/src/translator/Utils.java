package translator;

import java.io.*;
import java.util.ArrayList;

public class Utils {
    public static String joinStr(String delimiter, ArrayList<String> args) {
        if (args == null || args.isEmpty())
            return null;
        StringBuilder builder = new StringBuilder();
        int size = args.size() - 1;
        for (int i = 0; i < size; i++)
            builder.append(args.get(i)).append(delimiter);
        builder.append(args.get(size));
        return builder.toString();
    }

    public static String joinInt(String delimiter, ArrayList<Integer> args) {
        if (args == null || args.isEmpty())
            return null;
        StringBuilder builder = new StringBuilder();
        int size = args.size() - 1;
        for (int i = 0; i < size; i++)
            builder.append(args.get(i)).append(delimiter);
        builder.append(args.get(size));
        return builder.toString();
    }

    public static void writeFile (String fileName, String... data)
            throws IOException {
        File file = new File(fileName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (String line : data) {
            writer.append(line);
            writer.newLine();
        }
        writer.close();
    }
    public static ArrayList<String> readFile(String java13) throws IOException {
        File file = new File(java13);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArrayList<String> data = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null)
            if (!line.isEmpty())
                data.add(line);
        return data;
    }
}

