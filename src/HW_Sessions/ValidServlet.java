package HW_Sessions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ValidServlet extends HttpServlet {

    private final int N=3;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean remember = (req.getParameter("remember") != null);

        HttpSession session = req.getSession(true);


        //если не запоминать и пользователь еще не сохранялся в сессию
        if (!remember||(session.getAttribute(new String("UserName"))==null)) {

            String userName = req.getParameter("login");
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

                    session.setAttribute(new String("UserName"),userName);
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
                    if(session.isNew()){session.setAttribute(new String("ValueInvalidPass"),new Integer(0));}
                    Integer invalidPass = (Integer)session.getAttribute(new String("ValueInvalidPass"));
                    //N раз неправильно введенный пароль
                    System.out.println(invalidPass);
                    if(invalidPass>=N){
                        userList.remove(i);
                        try(FileWriter fileWriter = new FileWriter(file)){
                            for(User user:userList){
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(user.getName()).append("###");
                                stringBuilder.append(user.getPass());
                                String data = stringBuilder.toString();

                                fileWriter.write(data);
                                fileWriter.write('\n');
                            }
                        }
                        resp.setContentType("text/html");
                        String site = new String("http://localhost:8080/create.html");
                        resp.setStatus(resp.SC_MOVED_TEMPORARILY);
                        resp.setHeader("Location", site);
                    }
                    else{
                        session.setAttribute(new String("ValueInvalidPass"),invalidPass+1);
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid password");
                    }
                }
            }
        }
        else {
            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<h1>");
            stringBuilder.append("Hello, ");
            stringBuilder.append((String) session.getAttribute(new String("UserName")));
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
