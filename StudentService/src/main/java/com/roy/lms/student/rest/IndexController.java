package com.roy.lms.student.rest;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String index() {
        return "<!DOCTYPE html>\r\n"
        		+ "<html>\r\n"
        		+ "<head>\r\n"
        		+ "	<meta charset=\"utf-8\">\r\n"
        		+ "	<title>Index - Spring Rest API Microservice for Student Service</title>\r\n"
        		+ "	<script>\r\n"
        		+ "		function getIP(json) {\r\n"
        		+ "			hostIp = location.host;\r\n"
        		+ "			clientIp = json.ip;\r\n"
        		+ "			document.write('</br><center><div style=\"background:rgb(155, 225, 0);\"><b>Server IP - ' + hostIp + ' and Public IP of Client - ' + clientIp + '</b></div></center>');\r\n"
        		+ "		}\r\n"
        		+ "	</script>\r\n"
        		+ "	<script src=\"http://api.ipify.org?format=jsonp&callback=getIP\"></script>\r\n"
        		+ "</head>\r\n"
        		+ "    <body style=\"background:rgb(208, 208, 255);\">\r\n"
        		+ "        <h1>Welcome to Spring Rest API Microservice for Student Service!</h1>\r\n"
        		+ "        <p>Use Postman to continue using the REST API built using SpringBoot...</p>\r\n"
        		+ "    </body>\r\n"
        		+ "</html>";
    }
}
