package dev.junaaid96.jobs.security;

import dev.junaaid96.jobs.config.JobBoardProperties;
import dev.junaaid96.jobs.web.JobDtos.TokenRequest;
import dev.junaaid96.jobs.web.JobDtos.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

// Demo login endpoint that issues a signed JWT. Real systems usually delegate this to an identity provider.
@RestController
@RequestMapping("/api/v1/auth")
public class TokenController {

    private final AuthenticationManager authManager;
    private final JwtEncoder encoder;
    private final JobBoardProperties props;

    public TokenController(AuthenticationManager authManager, JwtEncoder encoder, JobBoardProperties props) {
        this.authManager = authManager;
        this.encoder = encoder;
        this.props = props;
    }

    @PostMapping("/token")
    public TokenResponse token(@Valid @RequestBody TokenRequest request) {
        // Throws AuthenticationException (-> 401) when credentials are wrong.
        var auth = authManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
        // Space-separated "scope" claim becomes SCOPE_* authorities on the resource server.
        String scope = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        var ttl = Duration.ofMinutes(props.tokenTtlMinutes());
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("job-board")
                .subject(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .claim("scope", scope)
                .build();
        // HS256 header must match the decoder's algorithm.
        var jwt = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims));
        return new TokenResponse(jwt.getTokenValue(), "Bearer", ttl.toSeconds());
    }
}
