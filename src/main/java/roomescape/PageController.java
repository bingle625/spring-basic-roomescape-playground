package roomescape;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import roomescape.member.LoginMember;
import roomescape.member.Member;

@Controller
public class PageController {
    @GetMapping("/admin")
    public String admin(LoginMember member, HttpServletResponse response) throws IOException {
        if (member.getRole().equals("USER")) {
            response.sendError(401);
            return null; // 상태 코드만 반환, 템플릿 렌더링 방지
        }
        return "admin/index";
    }

    @GetMapping("/admin/reservation")
    public String adminReservation() {
        return "admin/reservation";
    }

    @GetMapping("/admin/theme")
    public String adminTheme() {
        return "admin/theme";
    }

    @GetMapping("/admin/time")
    public String adminTime() {
        return "admin/time";
    }

    @GetMapping("/")
    public String reservation() {
        return "reservation";
    }

    @GetMapping("/reservation-mine")
    public String myReservation() {
        return "reservation-mine";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
}
