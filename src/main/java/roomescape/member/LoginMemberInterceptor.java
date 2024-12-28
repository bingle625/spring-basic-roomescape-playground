package roomescape.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginMemberInterceptor implements HandlerInterceptor {

  private final TokenUtil tokenUtil;
  private final MemberService memberService;

  public LoginMemberInterceptor(TokenUtil tokenUtil, MemberService memberService) {
    this.tokenUtil = tokenUtil;
    this.memberService = memberService;
  }
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Long memberId = this.tokenUtil.getMemberId(request.getCookies());

    Member member = this.memberService.find(memberId);

    if (member == null || !member.getRole().equals("ADMIN")) {
      response.setStatus(401);
      return false;
    }

    return true;
  }
}
