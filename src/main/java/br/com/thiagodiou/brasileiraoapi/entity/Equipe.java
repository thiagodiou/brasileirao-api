package br.com.thiagodiou.brasileiraoapi.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TB_EQP_EQUIPE")
public class Equipe implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EQP_ID")
	private Long id;
	
	@Column(name = "EQP_NM_NOME")
	private String nome;
	
	@Column(name = "EQP_DS_URL_LOGO")
	private String urlLogo;
}
