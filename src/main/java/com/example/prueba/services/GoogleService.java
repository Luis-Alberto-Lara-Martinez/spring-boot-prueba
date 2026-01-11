package com.example.prueba.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleService {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public GoogleService() {
        this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
        )
                .setAudience(Collections.singletonList(
                        "66886319688-repfu7bd9mt2jeo3me04t66nhoro77lv.apps.googleusercontent.com"))
                .build();
    }

    public GoogleIdToken.Payload verifyToken(String token) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = googleIdTokenVerifier.verify(token);
        if (idToken == null) throw new GeneralSecurityException("Token de Google inválido");
        return idToken.getPayload();
    }
}