package Spring.web.sem.web.principal;

import Spring.web.sem.web.model.DadosEpisodio;
import Spring.web.sem.web.model.DadosSerie;
import Spring.web.sem.web.model.DadosTemporadas;
import Spring.web.sem.web.model.Episodio;
import Spring.web.sem.web.service.ConsumoApi;
import Spring.web.sem.web.service.ConverterDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
//        System.out.println("\n TOP 10 Episódios");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro (N/A) " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação " + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento " + e))
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                    .map(d -> new Episodio(t.numero(), d))
                    ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do título do episódio ? ");
        var trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()) {
            System.out.println("Episodio encontrado!");
            System.out.println("Temporada : "+ episodioBuscado.get().getTemperoda());
        }else{
            System.out.println("Episodio não encontrado ! ");
        }

//        System.out.println("A parti de que ano voce quer ver os episodios ?");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano , 1 , 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada : " + e.getTemperoda() +
//                                "Episodio : " + e.getTitulo()+
//                                "Data Lançamento : " + e.getDataLancamento().format(formatador)
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemperoda,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: "+ est.getAverage());
        System.out.println("Melhor episódio : "+ est.getMax());
        System.out.println("Pior episódio : "+ est.getMin());
        System.out.println("Quantidade : "+ est.getCount());

    }
}
