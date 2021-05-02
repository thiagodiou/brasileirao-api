package br.com.thiagodiou.brasileiraoapi.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TB_PTD_PARTIDA")
public class Partida implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PTD_ID")
	private Long id;
	
	@Transient
	private String statusPartida;
	
	@ManyToOne
	@JoinColumn(name = "EQP_CASA_ID")
	private Equipe equipeCasa;
	
	@ManyToOne
	@JoinColumn(name = "EQP_VISITANTE_ID")
	private Equipe equipeVisitante;
	
	@Column(name = "PTD_NR_PLACAR_EQUIPE_CASA")
	private Integer placarEquipeCasa;
	
	@Column(name = "PTD_NR_PLACAR_EQUIPE_VISITANTE")
	private Integer placarEquipeVisitante;
	
	@Column(name = "PTD_DS_GOLS_EQUIPE_CASA")
	private String golsEquipeCasa;
	
	@Column(name = "PTD_DS_GOLS_EQUIPE_VISITANTE")
	private String golsEquipeVisitante;
	
	@Column(name = "PTD_NR_PLACAR_EST_EQUIPE_CASA")
	private Integer placarEstendindoEquipeCasa;
	
	@Column(name = "PTD_NR_PLACAR_EST_EQUIPE_VISITANTE")
	private Integer placarEstendidoEquipeVisitante;
	
	@ApiModelProperty(example = "dd/MM/yyyy HH:mm")
	@JsonFormat(pattern= "dd/MM/yyyy HH:mm", timezone = "America/Sao_Paulo")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PTD_DT_DATA_HORA_PARTIDA")
	private Date dataHoraPartida;
	
	@Column(name = "PTD_DS_LOCAL_PARTIDA")
	private String localPartida;
}
