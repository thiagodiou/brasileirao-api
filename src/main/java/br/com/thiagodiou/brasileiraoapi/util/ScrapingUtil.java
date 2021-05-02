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
import org.springframework.stereotype.Service;

import br.com.thiagodiou.brasileiraoapi.dto.PartidaGoogleDTO;

@Service
public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);

	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	
	private static final String DIV_PENALIDADES = "div[class=imso_mh_s__psn-sc]";
	
	private static final String DIV_GOL_EQUIPE_CASA = "div[class=imso_gs__tgs imso_gs__left-team]";
	private static final String DIV_GOL_EQUIPE_VISITANTE = "div[class=imso_gs__tgs imso_gs__right-team]" ;
	private static final String ITEM_GOL = "div[class=imso_gs__gs-r]";

	private static final String DIV_PLACAR_EQUIPE_CASA = "div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]";
	private static final String DIV_PLACAR_EQUIPE_VISITANTE = "div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]" ;
	
	private static final String DIV_LOGO_CASA = "div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]";
	private static final String DIV_LOGO_VISITANTE = "div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]";
	private static final String ITEM_LOGO = "img[class=imso_btl__mh-logo]";
	
	private static final String DIV_NOME_EQUIPE_CASA = "div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]";
	private static final String DIV_NOME_EQUIPE_VISITANTE = "div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]";
	
	private static final String DIV_PARTIDA_EM_ANDAMENTO = "div[class=imso_mh__lv-m-stts-cont]";
	private static final String DIV_PARTIDA_ENCERRADA ="span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]";
	
	private static final String CASA = "casa";
	private static final String VISITANTE = "visitante";
	private static final String HTTPS = "https:";
	private static final String SRC = "src";
	private static final String SPAN = "span";
	private static final String PENALTIS = "Pênaltis" ;

	public PartidaGoogleDTO obtemInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();

		Document document = null;
		try {
			document = Jsoup.connect(url).get();
			String title = document.title();
			LOGGER.info("Titulo da pagina: {}", title);

			StatusPartida statusPartida = obtemStatusPartida(document);
			partida.setStatusPartida(statusPartida.toString());
			LOGGER.info(statusPartida.toString());

			if (statusPartida != StatusPartida.PARTIDA_NAO_INICIADA) {
				String tempoPartida = obtemTempoPartida(document);
				partida.setTempoPartida(tempoPartida);
				LOGGER.info(tempoPartida);

				Integer placarEquipeCasa = recuperaPlacarEquipe(document, DIV_PLACAR_EQUIPE_CASA);
				partida.setPlacarEquipeCasa(placarEquipeCasa);
				LOGGER.info("Placar Equipe Casa: {}", placarEquipeCasa);

				Integer placarEquipeVisitante = recuperaPlacarEquipe(document, DIV_PLACAR_EQUIPE_VISITANTE);
				partida.setPlacarEquipeVisitante(placarEquipeVisitante);
				LOGGER.info("Placar Equipe Visitante: {}", placarEquipeVisitante);

				String golsEquipeCasa = recuperaGolsEquipe(document, DIV_GOL_EQUIPE_CASA);
				partida.setGolsEquipeCasa(golsEquipeCasa);
				LOGGER.info("Gols Equipe Casa: {}", golsEquipeCasa);

				String golsEquipeVisitante = recuperaGolsEquipe(document, DIV_GOL_EQUIPE_VISITANTE);
				partida.setGolsEquipeVisitante(golsEquipeVisitante);
				LOGGER.info("Gols Equipe Visitante: {}", golsEquipeVisitante);

				Integer placarEstendidoEquipeCasa = buscaPenalidades(document, CASA);
				partida.setPlacarEstendidoEquipeCasa(placarEstendidoEquipeCasa);
				LOGGER.info("Placar Estendido Equipe Casa: {}", placarEstendidoEquipeCasa);
				
				Integer placarEstendidoEquipeVisitante = buscaPenalidades(document, VISITANTE);
				partida.setPlacarEstendidoEquipeVisitante(placarEstendidoEquipeVisitante);
				LOGGER.info("Placar Estendido Equipe Visistante: {}", placarEstendidoEquipeVisitante);
			}

			String nomeEquipeCasa = recuperarNomeEquipe(document, DIV_NOME_EQUIPE_CASA);
			partida.setNomeEquipeCasa(nomeEquipeCasa);
			LOGGER.info("Nome Equipe Casa: {}", nomeEquipeCasa);

			String nomeEquipeVisitante = recuperarNomeEquipe(document, DIV_NOME_EQUIPE_VISITANTE);
			partida.setNomeEquipeVisitante(nomeEquipeVisitante);
			LOGGER.info("Nome Equipe Visitante: {}", nomeEquipeVisitante);

			String logoEquipeCasa = recuperarLogoEquipe(document, DIV_LOGO_CASA);
			partida.setUrlLogoEquipeCasa(logoEquipeCasa);
			LOGGER.info("Url Logo Equipe Casa: {}", logoEquipeCasa);

			String logoEquipeVisitante = recuperarLogoEquipe(document, DIV_LOGO_VISITANTE);
			partida.setUrlLogoEquipeVisitante(logoEquipeVisitante);
			LOGGER.info("Url Logo Equipe Visitante: {}", logoEquipeVisitante);
		} catch (IOException e) {
			LOGGER.error("ERRO AO TENTAR CONECTAR NO GOOGLE COM O JSOUP -> {}", e.getMessage());
		}

		return partida;
	}

	public Integer buscaPenalidades(Document document, String tipoEquipe) {
		boolean isPenalidades = document.select(DIV_PENALIDADES).isEmpty();
		if (!isPenalidades) {
			String penalidades = document.select(DIV_PENALIDADES).text();
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

	public String recuperaGolsEquipe(Document document, String itemHtml) {
		List<String> golsEquipe = new ArrayList<>();
		Elements elementos = document.select(itemHtml)
				.select(ITEM_GOL);

		for (Element e : elementos) {
			String infoGol = e.select(ITEM_GOL).text();
			golsEquipe.add(infoGol);
		}
		golsEquipe.toString();
		return String.join("; ", golsEquipe);
	}

	public Integer recuperaPlacarEquipe(Document document, String itemHtml) {
		String placarEquipeCasa = document.select(itemHtml).text();
		return formataPlacarStringInteger(placarEquipeCasa);
	}

	public String recuperarLogoEquipe(Document document, String itemHtml) {
		Element elemento = document.selectFirst(itemHtml);
		String urlLogo = HTTPS + elemento.select(ITEM_LOGO).attr(SRC);
		return urlLogo;
	}

	public String recuperarNomeEquipe(Document document, String itemHtml) {
		Element elemento = document.selectFirst(itemHtml);
		String nomeEquipe = elemento.select(SPAN).text();
		return nomeEquipe;
	}

	public StatusPartida obtemStatusPartida(Document document) {
		// situacoes:
		// 1 - partida nao iniciada
		// 2 - partida iniciada/jogo rolando/intervalo
		// 3 - partida encerrada
		// 4 - penalidades
		StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;

		boolean isTempoPartidaVazio = document.select(DIV_PARTIDA_EM_ANDAMENTO).isEmpty();

		if (!isTempoPartidaVazio) {
			String tempoPartida = document.select(DIV_PARTIDA_EM_ANDAMENTO).first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if (tempoPartida.contains(PENALTIS))
				LOGGER.info(tempoPartida);
		} else {
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}
		return statusPartida;
	}

	public String obtemTempoPartida(Document document) {
		String tempoPartida = null;

		// jogo rolando ou intervalo ou penalidades
		boolean isTempoPartidaVazio = document.select(DIV_PARTIDA_EM_ANDAMENTO).isEmpty();
		if (!isTempoPartidaVazio) {
			tempoPartida = document.select(DIV_PARTIDA_EM_ANDAMENTO).first().text();
		}

		// jogo encerrado
		isTempoPartidaVazio = document.select(DIV_PARTIDA_ENCERRADA)
				.isEmpty();
		if (!isTempoPartidaVazio) {
			tempoPartida = document.select(DIV_PARTIDA_ENCERRADA).first()
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
	
	public String montaUrlGoogle(String nomeEquipeCasa, String nomeEquipeVisitante) {
		try {
			String equipeCasa = nomeEquipeCasa.replace(" ", "+").replace("-", "+");
			String equipeVisitante = nomeEquipeVisitante.replace(" ", "+").replace("-", "+");
			return BASE_URL_GOOGLE + equipeCasa + "+x+" + equipeVisitante + COMPLEMENTO_URL_GOOGLE;
		}catch(Exception e) {
			LOGGER.error("ERRO: {}", e.getMessage() );
			return null;
		}
	}
}
