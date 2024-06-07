package tfip.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tfip.project.model.TelegramBot;
import tfip.project.service.MailService;

@SpringBootApplication
public class ProjectApplication implements CommandLineRunner {

	@Autowired
	TelegramBot bot;

	@Autowired
	MailService mailSvc;

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		// botsApi.registerBot(bot);

	}

}
