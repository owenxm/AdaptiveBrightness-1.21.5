package me.cominixo.adaptivebrightness.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.logging.Logger;

public class Config {

    public static double max_gamma = 1.0f;
    public static double min_gamma = 0.0f;
    public static boolean disabled = false;
//    public static boolean debug_disabled = false;
//    public static boolean print_settings = true;

    public static Screen init(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.adaptivebrightness.config"));

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.adaptivebrightness.general"));

        int max_gamma_percent = (int)(max_gamma*100);
        int min_gamma_percent = (int)(min_gamma*100);
        
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        general.addEntry(entryBuilder.startIntSlider(Text.translatable("option.adaptivebrightness.max_gamma"), max_gamma_percent, 0, 100)
                .setDefaultValue(100)
                .setTooltip(Text.translatable("option.adaptivebrightness.max_gamma.tooltip"))
                .setSaveConsumer(newValue -> max_gamma = newValue/100.0)
                .build());

        general.addEntry(entryBuilder.startIntSlider(Text.translatable("option.adaptivebrightness.min_gamma"), min_gamma_percent, 0, 100)
                .setDefaultValue(0)
                .setTooltip(Text.translatable("option.adaptivebrightness.min_gamma.tooltip"))
                .setSaveConsumer(newValue -> min_gamma = newValue/100.0)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.adaptivebrightness.disabled"), disabled)
                .setDefaultValue(disabled)
                .setTooltip(Text.translatable("option.adaptivebrightness.disabled.tooltip"))
                .setSaveConsumer(newValue -> disabled = newValue)
                .build());

//        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.adaptivebrightness.debug_disabled"), debug_disabled)
//                .setDefaultValue(debug_disabled)
//                .setTooltip(Text.translatable("option.adaptivebrightness.debug_disabled.tooltip"))
//                .setSaveConsumer(newValue -> debug_disabled = newValue)
//                .build());

//        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.adaptivebrightness.print_settings"), print_settings)
//                .setDefaultValue(print_settings)
//                .setTooltip(Text.translatable("option.adaptivebrightness.print_settings.tooltip"))
//                .setSaveConsumer(newValue -> print_settings = newValue)
//                .build());

        builder.setSavingRunnable(() -> {
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter("config/adaptivebrightness.conf");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.printf("max %f\n", max_gamma);
            printWriter.printf("min %f\n", min_gamma);
            printWriter.printf("disabled %b\n", disabled);
//            printWriter.printf("debug_disabled %b\n", debug_disabled);
//            printWriter.printf("print_settings %b\n", print_settings);
            printWriter.close();
        });

        return builder.build();

    }

    public static void load() throws IOException {
        FileReader configFile;

        try {
            configFile = new FileReader("config/adaptivebrightness.conf");
        } catch (FileNotFoundException ignored) {
            return;
        }

        BufferedReader reader = new BufferedReader(configFile);
        String line;
        while((line = reader.readLine())!=null) {
                   if (line.startsWith("max ")) {
                max_gamma = Double.parseDouble(line.split("max ")[1]);
            } else if (line.startsWith("min ")) {
                min_gamma = Double.parseDouble(line.split("min ")[1]);
            } else if (line.startsWith("disabled ")) {
                disabled = Boolean.parseBoolean(line.split("disabled ")[1]);
//            } else if (line.startsWith("debug_disabled ")) {
//                debug_disabled = Boolean.parseBoolean(line.split("debug_disabled ")[1]);
//            } else if (line.startsWith("print_settings ")) {
//                print_settings = Boolean.parseBoolean(line.split("print_settings ")[1]);
            }
        }
//        if (print_settings) {
//            System.out.println(max_gamma);
//            System.out.println(min_gamma);
//            System.out.println(disabled);
//            System.out.println(debug_disabled);
//            System.out.println(print_settings);
//        }

    }

}
