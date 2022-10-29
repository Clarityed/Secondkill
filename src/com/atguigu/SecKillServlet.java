package com.atguigu;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 秒杀案例
 */
public class SecKillServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SecKillServlet() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String userId = new Random().nextInt(50000) + "";
		String prodId =request.getParameter("prodId");
		
		boolean isSuccess=SecKill_redis.doSecKill(userId, prodId);
		response.getWriter().print(isSuccess);
	}

}
