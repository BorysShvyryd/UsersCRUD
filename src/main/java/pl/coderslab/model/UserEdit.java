package pl.coderslab.model;

import pl.coderslab.controler.User;
import pl.coderslab.controler.UserDao;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/user/edit")
public class UserEdit extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        User userDB = UserDao.getUser(Integer.parseInt(id));
        request.setAttribute("user", userDB);
        System.out.println(userDB);
        getServletContext().getRequestDispatcher("/users/edit.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User();
        user.setId(Integer.parseInt(request.getParameter("id")));
        user.setUserName(request.getParameter("userName"));
        user.setEmail(request.getParameter("userEmail"));
        user.setPassword(request.getParameter("userPassword"));

        UserDao.updateUser(user);
        response.sendRedirect(request.getContextPath() + "/user/list");
    }
}
