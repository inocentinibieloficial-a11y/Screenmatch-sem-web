package Spring.web.sem.web.principal;

import Spring.web.sem.web.model.DadosEpisodio;
import Spring.web.sem.web.model.DadosSerie;
import Spring.web.sem.web.model.DadosTemporadas;
import Spring.web.sem.web.model.Episodio;
import Spring.web.sem.web.service.ConsumoApi;
import Spring.web.sem.web.service.ConverterDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi  consumo = new ConsumoApi();

    private ConverterDados conversor = new ConverterDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=833beca8";

    public void exibirMenu(){
        System.out.println(" Digite o nome da Serie para buscar ");
        var nomeSerie = leitura.nextLine();
        var consumoapi = new ConsumoApi();
        var json = consumo.obterDados( ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporadas> temporadas = new ArrayList<>();

		for (int i =1; i<= dados.totalDeTemporadas(); i++){
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") +"&season="+i+ API_KEY);
			DadosTemporadas dadosTemporadas = conversor.obterDados(json, DadosTemporadas.class);
			temporadas.add(dadosTemporadas);
		}
            temporadas.forEach(System.out::println);
//        for(int i = 0; i < dados.totalDeTemporadas(); i++){
//            List<DadosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
//            for (int j = 0 ; j< episodiosTemporadas.size(); j++){
//                System.out.println(episodiosTemporadas.get(j).titulo());
//            }
//        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        //cria uma lista ultilizando flapmap e collecion

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());


        //cria um fitro do top 5 episodios da serie  do maior para o menor
        System.out.println("\n TOP 5 Episódios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                    .map(d -> new Episodio(t.numero(), d))
                    ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("A parti de que ano voce quer ver os episodios ?");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate dataBusca = LocalDate.of(ano , 1 , 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada : " + e.getTemperoda() +
                                "Episodio : " + e.getTitulo()+
                                "Data Lançamento : " + e.getDataLancamento().format(formatador)
                ));

    }
}
