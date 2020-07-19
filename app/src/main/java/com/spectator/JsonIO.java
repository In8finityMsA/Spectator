package com.spectator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class JsonIO {

    private File file;
    private JSONObject jsonFile;
    private String arrayKey;

    public JsonIO(File dir, String path) {
        this(dir, path, path);
    }

    public JsonIO(File dir, String path, String arrayKey) {
        this.file = new File(dir, path);
        this.arrayKey = arrayKey;
    }

    public JSONObject read() {
        if (!file.exists()) {
            jsonFile = createJSONObject(arrayKey);
            writeToFile();
        }
        try {
            jsonFile = new JSONObject(readJSONFromFile());
            return jsonFile;
        } catch (JSONException e) {
            e.printStackTrace();
            jsonFile = createJSONObject(arrayKey);
            writeToFile();
            return read();
        }
    }

    //Creating Object for JSON file
    private JSONObject createJSONObject(String arrayKey) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put(arrayKey, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //Adding a new object to JSON file
    //JsonObject.accumulate ???
    private void addObjectToJSON(JSONObject object, String arrayKey) {
        try {
            JSONArray jsonArray = jsonFile.getJSONArray(arrayKey);
            jsonArray.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Reading a whole JSON file
    private String readJSONFromFile() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    //Writing a whole JSON Object to a file
    public void writeToFile() {
        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(jsonFile.toString(1));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    //An attempt (fairly successful) to make writing to the end of JSON
    public void writeToEndOfFile(JSONObject object) {
        //Adding an Object to the Object of the whole JSON file
        if (object != null) {
            addObjectToJSON(object, arrayKey);

            StringBuilder stringBuilder = new StringBuilder();
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");) {

                randomAccessFile.seek(randomAccessFile.length() - 4);
                if ((char) randomAccessFile.read() != '[')
                    stringBuilder.append(",");
                randomAccessFile.seek(randomAccessFile.length() - 3);

                stringBuilder.append("\n");
                stringBuilder.append(object.toString(1));
                stringBuilder.append("\n]\n}");

                randomAccessFile.write(stringBuilder.toString().getBytes());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Finds an Object with specified key-value pair and replaces it with new Object
    public int replaceObject(JSONObject newObject, String oldObjectSearchKey, String oldObjectSearchValue) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonFile.getJSONArray(arrayKey);
            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.get(oldObjectSearchKey).equals(oldObjectSearchValue)) {
                    jsonArray.put(i, newObject);
                    writeToFile();
                    return 1;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteLast() {
        try {
            JSONArray jsonArray = jsonFile.getJSONArray(arrayKey);
            if (jsonArray.length() > 0) {
                jsonArray.remove(jsonArray.length() - 1);
            }
            writeToFile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseJsonArray(boolean isRereadJsonFile, ArrayList targetList, boolean isRewriteTargetList, Class<?> className, ArrayList<Class<?>> construstorArguments, String[] fieldNames) {
        JSONObject jsonObject;
        if (isRereadJsonFile) {
            jsonObject = read();
        }
        else {
            jsonObject = jsonFile;
        }
        if (isRewriteTargetList) {
            targetList = new ArrayList<>();
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("voters");
            Constructor<?> constructor = className.getConstructor();
            try {
                constructor.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonStage = jsonArray.getJSONObject(i);

                long timestamp = jsonStage.getLong("timestamp");
                int count = jsonStage.getInt("count");

                targetList.add(new Voter(timestamp, count));
            }

        } catch (JSONException | NoSuchMethodException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

}
