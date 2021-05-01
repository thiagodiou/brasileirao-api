package br.com.thiagodiou.brasileiraoapi.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaGoogleDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String statusPartida;
	private String tempoPartida;
	
	//informacoes equipe da casa
	private String nomeEquipeCasa;
	private String urlLogoEquipeCasa;
	private Integer placarEquipeCasa;
	private String golsEquipeCasa;
	private String placarEstendidoEquipeCasa;

	//informacoes equipe visitante
	private String nomeEquipeVisitante;
	private Integer placarEquipeVisitante;
	private String golsEquipeVisitante;
	private String placarEstendidoEquipeVisitante;
	

}
