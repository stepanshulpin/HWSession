package HW_Sessions;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ValidWithCookieServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Cookie[] cookies = req.getCookies();
        String userName=null;
        boolean newUser = true;
        for(Cookie cookie:cookies)
        {
            if(cookie.getName().equals("UserName")) {
                newUser=false;
                userName = cookie.getValue();
            }
        }
        if(newUser) {
            userName = req.getParameter("login");
            String userPass = req.getParameter("password");


            File file = new File(getServletContext().getRealPath("WEB-INF/user.txt"));
            List<User> userList = new ArrayList<>();

            try (FileReader fr = new FileReader(file);
                 BufferedReader reader = new BufferedReader(fr)) {

                String line = reader.readLine();
                while (line != null) {
                    StringTokenizer stok = new StringTokenizer(line, "###");
                    userList.add(new User(stok.nextToken(), stok.nextToken()));
                    line = reader.readLine();

                }
            }

            int i = userList.indexOf(new User(userName, userPass));

            if (i == -1) {
                resp.setContentType("text/html");
                // New location to be redirected
                String site = new String("http://localhost:8080/create.html");
                resp.setStatus(resp.SC_MOVED_TEMPORARILY);
                resp.setHeader("Location", site);
            } else {
                if (userList.get(i).getPass().equals(userPass)) {

                    Cookie userNameCookie = new Cookie("UserName", userName);
                    userNameCookie.setMaxAge(60 * 60 * 24);
                    resp.addCookie(userNameCookie);

                    resp.setContentType("text/html");
                    PrintWriter out = resp.getWriter();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("<h1>");
                    stringBuilder.append("Hello, ");
                    stringBuilder.append(userName);
                    stringBuilder.append("</h1>");
                    stringBuilder.append("<br/>");
                    stringBuilder.append("<h1 align=\"center\">");
                    stringBuilder.append("The password is correct");
                    stringBuilder.append("</h1>");
                    out.println(stringBuilder);
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid password");
                }
            }
        }
        else{
            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<h1>");
            stringBuilder.append("Hello, ");
            stringBuilder.append(userName);
            stringBuilder.append("</h1>");
            stringBuilder.append("<br/>");
            stringBuilder.append("<h1 align=\"center\">");
            stringBuilder.append("The password is correct");
            stringBuilder.append("</h1>");
            out.println(stringBuilder);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
