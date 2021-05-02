package br.com.thiagodiou.brasileiraoapi.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.thiagodiou.brasileiraoapi.dto.PartidaGoogleDTO;

public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);

	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";

	public static void main(String[] args) {
		String url = BASE_URL_GOOGLE + "union+espanola+x+audax+italiano" + COMPLEMENTO_URL_GOOGLE;
		ScrapingUtil scraping = new ScrapingUtil();
		scraping.obtemInformacoesPartida(url);
	}

	public PartidaGoogleDTO obtemInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();

		Document document = null;
		try {
			document = Jsoup.connect(url).get();
			String title = document.title();
			LOGGER.info("Titulo da pagina: {}", title);

			StatusPartida statusPartida = obtemStatusPartida(document);
			String tempoPartida = obtemTempoPartida(document);
			LOGGER.info(tempoPartida);
		} catch (IOException e) {
			LOGGER.error("ERRO AO TENTAR CONECTAR NO GOOGLE COM O JSOUP -> {}", e.getMessage());
		}

		return partida;
	}

	public StatusPartida obtemStatusPartida(Document document) {
		// situacoes:
		// 1 - partida nao iniciada
		// 2 - partida iniciada/jogo rolando/intervalo
		// 3 - partida encerrada
		// 4 - penalidades
		StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;

		boolean isTempoPartidaVazio = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();

		if (!isTempoPartidaVazio) {
			String tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if (tempoPartida.contains("Pênaltis"))
				LOGGER.info(tempoPartida);
		} else {
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}
		LOGGER.info(statusPartida.toString());
		return statusPartida;
	}

	public String obtemTempoPartida(Document document) {
		String tempoPartida = null;

		// jogo rolando ou intervalo ou penalidades
		boolean isTempoPartidaVazio = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		if (!isTempoPartidaVazio) {
			tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
		}

		// jogo encerrado
		isTempoPartidaVazio = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]")
				.isEmpty();
		if (!isTempoPartidaVazio) {
			tempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").first()
					.text();
		}

		return corrigeTempoPartida(tempoPartida);
	}

	private String corrigeTempoPartida(String tempo) {
		if (tempo.contains("'")) {
			return tempo.replace("'", " min");
		} else {
			return tempo;
		}
	}
}