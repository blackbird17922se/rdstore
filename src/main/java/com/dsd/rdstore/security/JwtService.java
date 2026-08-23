package com.dsd.rdstore.security;

import com.dsd.rdstore.model.Usuario;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;



@Service
public class JwtService {

    // llamamos los valores q tengo en application.properties a traves de su clave
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;



    /** generarToken(Usuario usuario) recibe al usuario que ya pasó correctamente el login */
    public String generarToken(Usuario usuario){

        Date ahora = new Date();

        // ahora + 1 hora (jwt.expiration=3600000) = fechaExpiracion
        Date fechaExpiracion = new Date(
            ahora.getTime() + expirationTime
        );

        return Jwts.builder()
            .subject(usuario.getNombreUsuario())

            .claim("idUsuario", usuario.getId())
            .claim("rol", usuario.getRol().getNombre())
            .issuedAt(ahora) 
            .expiration(fechaExpiracion)
            .signWith(getSigningKey())  
            .compact();
    }


    private SecretKey getSigningKey(){

        byte[] keyBytes = 
            secretKey.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extraerNombreUsuario(String token){
        return extraerClaims(token).getSubject();
    }


    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    private Claims extraerClaims(String token){

        return Jwts.parser() 
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validarToken(String token){
        try {
            extraerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
