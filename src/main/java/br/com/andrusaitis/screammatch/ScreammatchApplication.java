package br.com.andrusaitis.screammatch;

import br.com.andrusaitis.screammatch.Principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreammatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreammatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//instanciando a classe principal que esta consumindo a api e obtendo os dados
		Principal principal = new Principal();
		//Chamando o método dentro da classe que fara a tarefa
		principal.exibeMenu();
	}
}
