package roomescape.member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;


@Component
public class TokenUtil {

  private Key key;

  public TokenUtil() {
    this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  }

  public String generate(final Member member) {
    Date now = new Date();

    // duration 1시간으로 가정
    int durationSecond = 60 * 60;
    Date expirationDate = new Date(now.getTime() + 1000L * durationSecond);

    return Jwts.builder()
        .setSubject(member.getId().toString())
        .claim("name", member.getName())
        .claim("role", member.getName())
        .signWith(this.key)
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .compact();
  }

  public Long getMemberId(final Cookie[] cookies) {

    String token = this.extractTokenFromCookie(cookies);

    return Long.valueOf(Jwts.parserBuilder()
        .setSigningKey(this.key)
        .build()
        .parseClaimsJws(token)
        .getBody().getSubject());
  }

  private String extractTokenFromCookie(Cookie[] cookies) {
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("token")) {
        return cookie.getValue();
      }
    }

    return "";
  }
}
