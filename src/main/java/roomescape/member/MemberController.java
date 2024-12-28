package roomescape.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
  private final TokenUtil tokenUtil;
  private MemberService memberService;

  public MemberController(MemberService memberService, MemberDao memberDao, TokenUtil tokenUtil) {
    this.memberService = memberService;
    this.memberDao = memberDao;
    this.tokenUtil = tokenUtil;
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

    // TODO: member가 존재하지 않을때의 처리
    Member member = memberDao.findByEmailAndPassword(email, password);
    String token = tokenUtil.generate(member);

    // TODO: 쿠키를 header 에 정상적으로 넣도록 수정
    Cookie cookie = new Cookie("token", token);
    cookie.setHttpOnly(true);
    cookie.setPath("/");

    return ResponseEntity.ok().header("Set-Cookie", "token=" + token + ";").build();
  }

  @GetMapping("/login/check")
  public ResponseEntity<Map<String, String>> checkLogin(HttpServletRequest request) {
    Long memberId = tokenUtil.getMemberId(request.getCookies());
    Member member = memberDao.findById(memberId);

    return ResponseEntity.ok().body(Map.of("name", member.getName()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    Cookie cookie = new Cookie("token", "");
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    return ResponseEntity.ok().build();
  }
}
