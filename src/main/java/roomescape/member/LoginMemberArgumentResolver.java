package roomescape.member;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

  private final MemberService memberService;
  private final TokenUtil tokenUtil;

  public LoginMemberArgumentResolver(MemberService memberService, TokenUtil tokenUtil) {
    this.memberService = memberService;
    this.tokenUtil = tokenUtil;
  }

  @Override
  public boolean supportsParameter(final MethodParameter parameter) {
    return parameter.getParameterType().equals(LoginMember.class);

  }

  @Override
  public Object resolveArgument(
      final MethodParameter parameter,
      final ModelAndViewContainer mavContainer,
      final NativeWebRequest webRequest,
      final WebDataBinderFactory binderFactory)
      throws Exception {
    HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
    Long memberId = tokenUtil.getMemberId(request.getCookies());

    Member member = memberService.find(memberId);
    return new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());

  }
}
