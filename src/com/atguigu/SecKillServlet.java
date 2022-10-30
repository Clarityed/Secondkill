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
		String prodId = request.getParameter("prodId");

		// 此方法未解决库存遗留问题
		// boolean isSuccess = SecKill_redis.doSecKill(userId, prodId);
		// 此方法解决库存遗留问题
		boolean isSuccess = SecKill_redisByScript.doSecKill(userId, prodId);
		response.getWriter().print(isSuccess);
	}

}
