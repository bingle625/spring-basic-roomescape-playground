package roomescape.member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class MemberController {

    private final MemberDao memberDao;
    private MemberService memberService;

    private Key key;

    public MemberController(MemberService memberService, MemberDao memberDao) {
        this.memberService = memberService;
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.memberDao = memberDao;
    }

    @PostMapping("/members")
    public ResponseEntity createMember(@RequestBody MemberRequest memberRequest) {
        MemberResponse member = memberService.createMember(memberRequest);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).body(member);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody Map<String, String> body) {
        // HttpServletRequest 가 Request 객체가 아닌가? 일단 Map으로 대체
        String email = body.get("email");
        String password = body.get("password");
        Date now = new Date();

        // duration 1시간으로 가정
        int durationSecond = 60 * 60;
        Date expirationDate = new Date(now.getTime() + 1000L * durationSecond);

        // TODO: member가 존재하지 않을때의 처리
        Member member = this.memberDao.findByEmailAndPassword(email, password);

        String token = Jwts.builder()
            .setSubject(member.getId().toString())
            .claim("name", member.getName())
            .claim("role", member.getName())
            .signWith(this.key)
            .setIssuedAt(now)
            .setExpiration(expirationDate)
            .compact();


        // TODO: 쿠키를 header 에 정상적으로 넣도록 수정
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return ResponseEntity.ok().header("Set-Cookie", "token=" + token + ";").build();
    }

    @GetMapping("/login/check")
    public ResponseEntity checkLogin(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        String token = this.extractTokenFromCookie(cookies);

        Long memberId = Long.valueOf(Jwts.parserBuilder()
            .setSigningKey(this.key)
            .build()
            .parseClaimsJws(token)
            .getBody().getSubject());

        Member member = this.memberDao.findById(memberId);

        return ResponseEntity.ok().body(Map.of("name", member.getName()));
    }

    private String extractTokenFromCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                return cookie.getValue();
            }
        }

        return "";
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}
