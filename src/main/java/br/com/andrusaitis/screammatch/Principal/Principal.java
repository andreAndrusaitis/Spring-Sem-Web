package br.com.andrusaitis.screammatch.Principal;

import br.com.andrusaitis.screammatch.model.DadosEpisodio;
import br.com.andrusaitis.screammatch.model.DadosSerie;
import br.com.andrusaitis.screammatch.model.DadosTemporada;
import br.com.andrusaitis.screammatch.model.Episodio;
import br.com.andrusaitis.screammatch.service.ConsumoApi;
import br.com.andrusaitis.screammatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    //Sacanner para leitura
    private Scanner leitura = new Scanner(System.in);

    //Colocando o consumo da apr aqui na principal
    private ConsumoApi consumo = new ConsumoApi();

    //Colocando o consumo do conversor
    private ConverteDados conversor = new ConverteDados();

    //Atribuindo variaveis para o endereço e a api key finais que não serão modificadas
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=68e6c527";

    public void exibeMenu(){
        System.out.println("Digite o nome da serie desejada!");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO+nomeSerie.replace(" ", "+")+API_KEY);
        DadosSerie dados = conversor.obterDados(json,DadosSerie.class);
        System.out.println(dados);

        //Lista de temporadas
		List<DadosTemporada> temporadas = new ArrayList<>();

		//Implementando um loop for para pegar todas as temporadas
		for (int i = 1; i <= dados.totalTemporadas() ; i++) {
			json = consumo.obterDados(ENDERECO+nomeSerie.replace(" ", "+")+"&season="+i+API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json,DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		//Imprimindo a lista de temporadas
		temporadas.forEach(System.out::println);

        //Pegando apenas os episodios de cada temporada método antigo e "novo"
//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporadas.size(); j++) {
//                System.out.println(episodiosTemporadas.get(j).titulo());
//            }
//        }
        temporadas.forEach(t -> t.episodios().forEach(e-> System.out.println(e.titulo())));

        //Criando lista para trabalhar com Strems
        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())//Fluxo de dados dos episodios das temporadas
                .collect(Collectors.toList());//Coloca a lista de ep dentro da lista dadosEp

        System.out.println("\n Top 5 Episódios");
        dadosEpisodios.stream()
                .filter(e->!e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        //Lista de episodios que transforma os Eps de cada temporada
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        //Colocando Scam para filtar ano de visualização dos episodios
        System.out.println("A partir de que ano, você gostaria de ver os Episodios?");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate dataBusca = LocalDate.of(ano,1,1);

        //Formatador de datas
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada()
                        + " Episódio: " + e.getTitulo()
                        + " Data de lançamento: " + e.getDataLancamento().format(formatador)
                ));
    }
}
