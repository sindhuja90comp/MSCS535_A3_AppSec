import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class SecurityFilter implements Filter {

    // These are the safety limits used by the filter.
    private static final int MAX_REQUEST_SIZE = 8 * 1024;
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final Pattern SAFE_INPUT = Pattern.compile("^[a-zA-Z0-9 .,@_-]*$");

    private final Map<String, ClientWindow> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Add browser safety rules to every response.
        addSecurityHeaders(res);

        // Only allow basic request types.
        if (!isAllowedMethod(req)) {
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP method not allowed");
            return;
        }

        // Stop very large requests.
        if (req.getContentLengthLong() > MAX_REQUEST_SIZE) {
            res.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "Request too large");
            return;
        }

        String clientIp = getClientIp(req);
        // Stop one user from sending too many requests too fast.
        if (!checkRateLimit(clientIp)) {
            res.sendError(HttpServletResponse.SC_TOO_MANY_REQUESTS, "Rate limit exceeded");
            return;
        }

        // Check that the user input looks safe.
        if (!validateParameters(req)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
            return;
        }

        // Let the request continue if all checks pass.
        chain.doFilter(request, response);
    }

    private void addSecurityHeaders(HttpServletResponse res) {
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("Referrer-Policy", "no-referrer");
        res.setHeader("Content-Security-Policy", "default-src 'self'; frame-ancestors 'none'; object-src 'none'");
        res.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
        res.setHeader("Cache-Control", "no-store");
        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
    }

    private boolean isAllowedMethod(HttpServletRequest req) {
        String method = req.getMethod();
        return "GET".equals(method) || "POST".equals(method);
    }

    private boolean validateParameters(HttpServletRequest req) {
        // Look at each input value sent by the user.
        for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
            for (String value : entry.getValue()) {
                if (value == null || value.length() > 256 || !SAFE_INPUT.matcher(value).matches()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkRateLimit(String clientIp) {
        long now = System.currentTimeMillis();
        ClientWindow window = rateLimitMap.computeIfAbsent(clientIp, ip -> new ClientWindow(now));

        synchronized (window) {
            // Start a new one-minute count when the old one ends.
            if (now - window.windowStart > 60_000) {
                window.windowStart = now;
                window.counter.set(0);
            }
            return window.counter.incrementAndGet() <= MAX_REQUESTS_PER_MINUTE;
        }
    }

    private String getClientIp(HttpServletRequest req) {
        // Use the real visitor IP when the app sits behind another server.
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    private static class ClientWindow {
        volatile long windowStart;
        AtomicInteger counter = new AtomicInteger(0);

        ClientWindow(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
