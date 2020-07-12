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
import java.util.ArrayList;

public class WorkWithJson {

    private File file;
    private JSONObject jsonFile;

    public WorkWithJson(File dir, String path) {
        this.file = new File(dir, path);
    }

    public ArrayList<Voter> firstInit() throws JSONException {
        if (file.exists()) {
            jsonFile = new JSONObject(readJSONFromFile());
            return parseJson(jsonFile);
        }
        else {
            createJSONObject();
            writeToFile();
            return new ArrayList<Voter>();
        }
    }

    //Creating Object for JSON file
    private void createJSONObject() {
        jsonFile = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonFile.put("voters", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Adding a voter to JSON array
    private JSONObject createVoter(Voter voter) throws JSONException {
        JSONObject newVoter = new JSONObject();
        newVoter.put("timestamp", voter.getTime());
        newVoter.put("count", voter.getCount());
        newVoter.put("formattedDate", voter.getFormattedDate());
        newVoter.put("formattedTime", voter.getFormattedTime());
        newVoter.put("text", voter.getText());
        return newVoter;
    }

    //Adding a voter to JSON file Object
    public void addVoterToJSON(Voter voter, String arrayKey) {
        try {
            JSONArray jsonArray = jsonFile.getJSONArray(arrayKey);
            jsonArray.put(createVoter(voter));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Reading a whole JSON file
    public String readJSONFromFile() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void writeToEndOfFile(Voter voter) {
        RandomAccessFile randomAccessFile = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(randomAccessFile.length() - 4);
            if ((char) randomAccessFile.read() != '[')
                stringBuilder.append(",");
            randomAccessFile.seek(randomAccessFile.length() - 3);
            stringBuilder.append("\n" + createVoter(voter).toString(1) + " \n]\n}");
            randomAccessFile.write(stringBuilder.toString().getBytes());
            randomAccessFile.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /*An attempt to make writing to the end of JSON
    public void writeJSONToFile(JSONObject jsonObject, String path, boolean isAppend) {
        try {
            File file = new File(this.getFilesDir(), path);
            FileWriter fileWriter = new FileWriter(file, isAppend);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonObject.toString(1));
            bufferedWriter.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }*/

    //Parsing voters from JSON Object and adding them to list
    public ArrayList<Voter> parseJson(JSONObject response) {
        ArrayList<Voter> voters = new ArrayList<Voter>();
        try {
            JSONArray stageArray = response.getJSONArray("voters");

            for (int i = 0; i < stageArray.length(); i++) {
                JSONObject jsonStage = (JSONObject) stageArray.get(i);

                long timestamp = jsonStage.getLong("timestamp");
                int count = jsonStage.getInt("count");

                Voter voter = new Voter(timestamp, count);

                voters.add(voter);
            }

        } catch (JSONException e) {
            e.getMessage();
            e.printStackTrace();
        }

        return voters;
    }

}
