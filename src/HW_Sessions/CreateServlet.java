package HW_Sessions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CreateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String userName = req.getParameter("login");
        String userPass = req.getParameter("password");
        File file = new File(getServletContext().getRealPath("WEB-INF/user.txt"));
        List<User> userList = new ArrayList<>();
        try(FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr)) {
            String line = reader.readLine();
            while (line != null) {
                StringTokenizer stok = new StringTokenizer(line, "###");
                userList.add(new User(stok.nextToken(), stok.nextToken()));
                line = reader.readLine();
            }
        }
        int i=userList.indexOf(new User(userName,userPass));

        if(i==-1){
            userList.add(new User(userName,userPass));
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
            PrintWriter out = resp.getWriter();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<h1>");
            stringBuilder.append("Hello, ");
            stringBuilder.append(userName);
            stringBuilder.append("</h1>");
            stringBuilder.append("<br/>");
            stringBuilder.append("<h1 align=\"center\">");
            stringBuilder.append("Created");
            stringBuilder.append("</h1>");
            out.println(stringBuilder);
        }else{
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username already exists");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
