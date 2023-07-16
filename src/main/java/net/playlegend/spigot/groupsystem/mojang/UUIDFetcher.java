package net.playlegend.spigot.groupsystem.mojang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UUIDFetcher {

    static final ExecutorService pool = Executors.newCachedThreadPool();

    static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    UUID id;

    public static UUID getUUID(String name){
        if (name == null) return null;

        name = name.toLowerCase();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, System.currentTimeMillis()/1000)).openConnection();
            connection.setReadTimeout(5000);

            CompletableFuture<UUIDFetcher> dataFuture = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);
                        } catch (IOException e) {
                            return null;
                        }
                    }, pool
            );

            UUIDFetcher data = dataFuture.get();

            if (data == null) {
                return null;
            }

            return data.id;
        }catch (Exception e){
            return null;
        }
    }

}
