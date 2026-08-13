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

        // Esto obtiene la hora en la que estamos creando el token.
        Date ahora = new Date();

        // ahora + 1 hora (jwt.expiration=3600000) = fechaExpiracion
        Date fechaExpiracion = new Date(
            ahora.getTime() + expirationTime
        );

        return Jwts.builder() // estas diciendo algo como "Voy a construir un JWT."
            .subject(usuario.getNombreUsuario()) //Define al usuario a quién pertenece el token.

            // claim agrega información adicional:
            .claim("idUsuario", usuario.getId())
            .claim("rol", usuario.getRol().getNombre())
            /* Así el contenido conceptual del token será:
                {
                    "sub": "yes",
                    "idUsuario": 17,
                    "rol": "ADMIN"
                }
             */
            .issuedAt(ahora) // issuedAt = cuándo se creó.
            .expiration(fechaExpiracion) // expiration = cuándo deja de ser válido.
            .signWith(getSigningKey()) // Aquí es donde firmamos el token con nuestra clave secreta.
            .compact(); //convierte todo lo construido en el String JWT
    }


    private SecretKey getSigningKey(){

        /* Nuestra clave (secretKey) actualmente es un String...
        Pero JJWT necesita una clave criptográfica, un SecretKey.
        por eso se toma secretKey, se obtiene su valor en bytes con getBytes
        y se guarda ya tranformado en bytes en keyBytes
        */

        byte[] keyBytes = 
            secretKey.getBytes(StandardCharsets.UTF_8);

        /* con Keys.hmacShaKeyFor JJWT toma esos bytes y los tranforma en una SecretKey
        la cual retorna este metodo y que se puede utilizar para firma */
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extraerNombreUsuario(String token){
        return extraerClaims(token).getSubject();
    }

    // del claim obtiene el rol, mirar el comentario de mas abajo
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    /* Claims es el objeto Java que representa esta información q generamos en el JWT
        {
        "sub": "mauro92",
        "idUsuario": 2,
        "rol": "ADMIN",
        "iat": 1786076674,
        "exp": 1786080274
        }
   */
    private Claims extraerClaims(String token){

        return Jwts.parser() //para leer/interpretar JWT.
            .verifyWith(getSigningKey()) // Para comprobar este token, utiliza nuestra clave secreta
            .build()
            .parseSignedClaims(token) //JWT que contiene claims y está firmado.
            .getPayload(); // .getPayload() nos devuelve ese contenido como: Claims
    }

    public boolean validarToken(String token){
        try {
            // Intenta procesar el JWT. Si llegas hasta el final sin que ocurra ningún error, 
            // lo considero válido
            extraerClaims(token);
            // ¿Pude procesar correctamente este token?:
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
}
