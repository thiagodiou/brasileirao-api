package br.com.thiagodiou.brasileiraoapi.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.thiagodiou.brasileiraoapi.dto.PartidaGoogleDTO;

public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);

	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	private static final String CASA = "casa";
	private static final String VISITANTE = "visitante";

	public static void main(String[] args) {
		String url = BASE_URL_GOOGLE + "palmeiras+x+corinthians+08/08/2020" + COMPLEMENTO_URL_GOOGLE;
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
			LOGGER.info(statusPartida.toString());

			if (statusPartida != StatusPartida.PARTIDA_NAO_INICIADA) {
				String tempoPartida = obtemTempoPartida(document);
				LOGGER.info(tempoPartida);

				Integer placarEquipeCasa = recuperaPlacarEquipeCasa(document);
				LOGGER.info("Placar Equipe Casa: {}", placarEquipeCasa);

				Integer placarEquipeVisitante = recuperaPlacarEquipeVisitante(document);
				LOGGER.info("Placar Equipe Visitante: {}", placarEquipeVisitante);

				String golsEquipeCasa = recuperaGolsEquipeCasa(document);
				LOGGER.info("Gols Equipe Casa: {}", golsEquipeCasa);

				String golsEquipeVisitante = recuperaGolsEquipeVisitante(document);
				LOGGER.info("Gols Equipe Visitante: {}", golsEquipeVisitante);

				Integer placarEstendidoEquipeCasa = buscaPenalidades(document, CASA);
				LOGGER.info("Placar Estendido Equipe Casa: {}", placarEstendidoEquipeCasa);
				
				Integer placarEstendidoEquipeVisitante = buscaPenalidades(document, VISITANTE);
				LOGGER.info("Placar Estendido Equipe Visistante: {}", placarEstendidoEquipeVisitante);
			}

			String nomeEquipeCasa = recuperarNomeEquipeCasa(document);
			LOGGER.info("Nome Equipe Casa: {}", nomeEquipeCasa);

			String nomeEquipeVisitante = recuperarNomeEquipeVisitante(document);
			LOGGER.info("Nome Equipe Visitante: {}", nomeEquipeVisitante);

			String logoEquipeCasa = recuperarLogoEquipeCasa(document);
			LOGGER.info("Url Logo Equipe Casa: {}", logoEquipeCasa);

			String logoEquipeVisitante = recuperarLogoEquipeVisitante(document);
			LOGGER.info("Url Logo Equipe Visitante: {}", logoEquipeVisitante);
		} catch (IOException e) {
			LOGGER.error("ERRO AO TENTAR CONECTAR NO GOOGLE COM O JSOUP -> {}", e.getMessage());
		}

		return partida;
	}

	public Integer buscaPenalidades(Document document, String tipoEquipe) {
		boolean isPenalidades = document.select("div[class=imso_mh_s__psn-sc]").isEmpty();
		if (!isPenalidades) {
			String penalidades = document.select("div[class=imso_mh_s__psn-sc]").text();
			String penalidadesCompleta = penalidades.substring(0, 5).replace(" ", "");
			String[] divisao = penalidadesCompleta.split("-");

			return tipoEquipe.equals(CASA) ? formataPlacarStringInteger(divisao[0]) : formataPlacarStringInteger(divisao[1]);

		}
		return null;
	}

	public Integer formataPlacarStringInteger(String placar) {
		Integer valor;
		try {
			valor = Integer.parseInt(placar);
		} catch (Exception e) {
			valor = 0;
		}
		return valor;
	}

	public String recuperaGolsEquipeVisitante(Document document) {
		List<String> golsEquipe = new ArrayList<>();
		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__right-team]")
				.select("div[class=imso_gs__gs-r]");

		for (Element e : elementos) {
			String infoGol = e.select("div[class=imso_gs__gs-r]").text();
			golsEquipe.add(infoGol);
		}
		golsEquipe.toString();
		return String.join("; ", golsEquipe);
	}

	public String recuperaGolsEquipeCasa(Document document) {
		List<String> golsEquipe = new ArrayList<>();
		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__left-team]")
				.select("div[class=imso_gs__gs-r]");

		for (Element e : elementos) {
			String infoGol = e.select("div[class=imso_gs__gs-r]").text();
			golsEquipe.add(infoGol);
		}
		golsEquipe.toString();
		return String.join("; ", golsEquipe);
	}

	public Integer recuperaPlacarEquipeCasa(Document document) {
		String placarEquipeCasa = document.select("div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]").text();
		return formataPlacarStringInteger(placarEquipeCasa);
	}

	public Integer recuperaPlacarEquipeVisitante(Document document) {
		String placarEquipeVisitante = document.select("div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]")
				.text();
		return formataPlacarStringInteger(placarEquipeVisitante);
	}

	public String recuperarLogoEquipeCasa(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + elemento.select("img[class=imso_btl__mh-logo]").attr("src");
		return urlLogo;
	}

	public String recuperarLogoEquipeVisitante(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + elemento.select("img[class=imso_btl__mh-logo]").attr("src");
		return urlLogo;
	}

	public String recuperarNomeEquipeVisitante(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		String nomeEquipe = elemento.select("span").text();
		return nomeEquipe;
	}

	public String recuperarNomeEquipeCasa(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		String nomeEquipe = elemento.select("span").text();
		return nomeEquipe;
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
