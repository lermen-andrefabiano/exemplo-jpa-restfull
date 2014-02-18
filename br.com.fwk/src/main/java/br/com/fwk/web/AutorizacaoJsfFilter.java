package br.com.fwk.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AutorizacaoJsfFilter implements Filter {

	public static final int SESSION_EXPIRED = 450;

	private String redirectUrl;
	private String exclude;
	private String testRedirect;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		exclude = filterConfig.getInitParameter("exclude");
		redirectUrl = filterConfig.getInitParameter("redirectUrl");

		// redirectUrl =
		// filterConfig.getServletContext().getInitParameter("redirectUrl");
		if (redirectUrl == null) {
			System.err
					.println("Variavel 'redirectUrl' não definida para AutorizacaoJsfFilter!");
		}

		if (redirectUrl != null) {
			testRedirect = redirectUrl.replaceAll("\\?.*", "");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(false);

		if ((session != null && session.getAttribute("login") != null)
				|| validarUri(req.getRequestURI())) {
			chain.doFilter(request, response);
		} else {

			if (session != null) {
				System.err.println("NAO AUTORIZADO: " + req.getRequestURI());
			} else {
				System.err.println("Sessao expirada");
			}

			HttpServletResponse res = (HttpServletResponse) response;

			String jsfAjax = req.getParameter("javax.faces.partial.ajax");
			if ((jsfAjax != null && jsfAjax.equals("true"))
					|| redirectUrl == null) {
				if (session != null) {
					res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				} else {
					res.sendError(SESSION_EXPIRED); // sessao expirou
				}
			} else {
				if (session != null) {
					res.sendRedirect(req.getContextPath() + "/" + redirectUrl
							+ "?unauthorized");
				} else {
					res.sendRedirect(req.getContextPath() + "/" + redirectUrl
							+ "?session_expired");
				}
			}

		}

	}

	private boolean validarUri(String uri) {

		if (exclude != null && uri.matches(exclude)) {
			return true;
		}

		if (uri.matches(".+\\.(js|css|jpg|jpeg|png|gif|ico)")
				|| uri.contains("javax.faces.resource")
				|| (testRedirect != null && uri.contains(testRedirect))) {
			return true;
		}

		return false;
	}

	@Override
	public void destroy() {
	}

}
