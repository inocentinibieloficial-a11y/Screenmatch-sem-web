package Spring.web.sem.web;

import Spring.web.sem.web.model.DadosSerie;
import Spring.web.sem.web.service.ConsumoApi;
import Spring.web.sem.web.service.ConverterDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenMatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenMatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		var consumoapi = new ConsumoApi();
		var json = consumoapi.obterDados( "https://www.omdbapi.com/?t=The-Flash&apikey=833beca8");
		System.out.printf(json);

		ConverterDados conversor = new ConverterDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);
	}
}
