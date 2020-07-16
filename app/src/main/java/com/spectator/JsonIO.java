package com.spectator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class JsonIO {

    private File file;
    private JSONObject jsonFile;
    private String arrayKey;

    public JsonIO(File dir, String path) {
        this.file = new File(dir, path);
        this.arrayKey = path;
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
        } catch (IOException | JSONException e) {
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
    public void addObjectToJSON(JSONObject object, String arrayKey) {
        try {
            JSONArray jsonArray = jsonFile.getJSONArray(arrayKey);
            jsonArray.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Reading a whole JSON file
    public String readJSONFromFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        fileReader.close();
        return stringBuilder.toString();
    }

    //Writing a whole JSON Object to a file
    public void writeToFile() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonFile.toString(1));
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    //An attempt to make writing to the end of JSON
    public void writeToEndOfFile(JSONObject object) {
        //Adding an Object to the Object of the whole JSON file
        addObjectToJSON(object, arrayKey);

        RandomAccessFile randomAccessFile = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(randomAccessFile.length() - 4);
            if ((char) randomAccessFile.read() != '[')
                stringBuilder.append(",");
            randomAccessFile.seek(randomAccessFile.length() - 3);

            stringBuilder.append("\n");
            stringBuilder.append(object.toString(1));
            stringBuilder.append("\n]\n}");

            randomAccessFile.write(stringBuilder.toString().getBytes());
            randomAccessFile.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    //Finds an Object with specified key-value pair and replaces it with new Object
    public int replaceObject(JSONObject newObject, String oldObjectSearchKey, String oldObjectSearchValue) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonFile.getJSONArray(arrayKey);
            for (int i = 0; i < jsonArray.length(); i++) {
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

}
