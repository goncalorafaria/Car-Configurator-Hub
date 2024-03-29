package CCH.business;

import CCH.dataaccess.ComponenteDAO;
import CCH.dataaccess.PacoteDAO;
import CCH.dataaccess.RemoteClass;
import CCH.dataaccess.UtilizadorDAO;
import CCH.exception.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Classe principal da aplicação Car Configurator Hub.
 *
 * @version 20181229
 */

public class CCH {
	private GestaoDeConfiguracao gestaoDeConfiguracao;
	private OperacaoFabril operacaoFabril;
	private UtilizadorDAO utilizadorDAO;
	private PacoteDAO pacoteDAO;
	private ComponenteDAO componenteDAO;

	/**
	 * Construtor por omissão de CCH.
	 */
	public CCH() {
		this.operacaoFabril = new OperacaoFabril();
		this.gestaoDeConfiguracao = new GestaoDeConfiguracao();
		this.utilizadorDAO = new UtilizadorDAO();
		this.pacoteDAO = new PacoteDAO();
		this.componenteDAO = new ComponenteDAO();
	}

	/**
	 * Devolve a GestaoDeConfiguracao, que possui as configurações e encomendas
	 * do sistema.
	 *
	 * @return GestaoDeConfiguracao
	 */
	public GestaoDeConfiguracao getGestaoDeConfiguracao() {
		return gestaoDeConfiguracao;
	}

	/**
	 * Atualiza a GestaoDeConfiguracao do sistema.
	 *
	 * @param gestaoDeConfiguracao GestaoDeConfiguracao com as informações
	 * das configurações e encomendas
	 */
	public void setGestaoDeConfiguracao(GestaoDeConfiguracao gestaoDeConfiguracao) {
		this.gestaoDeConfiguracao = gestaoDeConfiguracao;
	}

	/**
	 * Devolve a OperacaoFabril, que possui as informações dos componentes e das
	 * encomendas.
	 *
	 * @return OperacaoFabril

	/**
	 * Método que permite que o utilizador aceda à aplicação.
	 *
	 * @param id Id do utilizador
	 * @param password Password do utilizador
	 * @return Utilizador que iniciou sessão
	 * @throws WrongCredentialsException Caso o par das credenciais inseridas
	 * não corresponda a nenhum utilizador registado na aplicação
	 */
	public Utilizador iniciarSessao(int id, String password) throws WrongCredentialsException {
		Utilizador utilizador = utilizadorDAO.get(id);
		if (utilizador == null)
			throw new WrongCredentialsException();

		boolean loggedIn = utilizador.validarCredenciais(id, password);
		if (!loggedIn)
			throw new WrongCredentialsException();

		return utilizador;
	}

	/**
	 * Método que cria um novo pacote no sistema.
	 *
	 * @return Pacote criado
	 */
	public Pacote criarPacote() {
		int id = pacoteDAO.getNextId();
		Pacote pacote = new Pacote(id, 0);
		pacote = pacoteDAO.put(id, pacote);
		return pacote;
	}

	/**
	 * Método que remove um pacote do sistema.
	 *
	 * @param pacoteId Id do pacote que se pretende eliminar
	 */
	public void removerPacote(int pacoteId) {
		gestaoDeConfiguracao.removePacote(pacoteId, pacoteDAO.getDescontoPacote(pacoteId));

		pacoteDAO.removeAllComponentes(pacoteId);
		pacoteDAO.remove(pacoteId);
	}


	/**
	 * Método que cria um novo utilizador no sistema.
	 *
	 * @return Utilizador criado
	 */
	public Utilizador criarUtilizador() {


		Integer id = utilizadorDAO.getNextId();
		Utilizador utilizador = new Utilizador(id,"empty", "empty");
		utilizador = utilizadorDAO.put(utilizador.getId(), utilizador);
		return utilizador;
	}

	/**
	 * Método que remove um utilizador do sistema.
	 *
	 * @param utilizadorId Id do utilizador que se pretende eliminar
	 */
	public void removerUtilizador(int utilizadorId) {
		this.utilizadorDAO.remove(utilizadorId);
	}

	/**
	 * Método que devolve todos os utilizadores no sistema.
	 *
	 * @return List<Utilizador> Lista de todos os utilizadores no sistema
	 */
	public List<Utilizador> consultarFuncionarios() {
		return this.utilizadorDAO.values().stream().
                    map(l -> (Utilizador)l).collect(Collectors.toList());
	}

	/**
	 * Método que devolve todos os pacotes no sistema.
	 *
	 * @return List<Pacote> Lista de todos os pacotes no sistema
	 */
	public List<Pacote> consultarPacotes() {
		return this.pacoteDAO.values().stream().
                    map(l -> (Pacote) l).collect(Collectors.toList());
	}

	/**
	 * Método que devolve todos os componentes no sistema.
	 *
	 * @return List<Componente> Lista de todos os componentes no sistema
	 */
	public List<Componente> consultarComponentes() {
		return this.componenteDAO.values().stream().
                    map(l -> (Componente)l).collect(Collectors.toList());
	}


	/**
	 * Método que devolve todos as configurações no sistema.
	 *
	 * @return List<Configurações> Lista de todos as configurações no sistema
	 */

	public List<Configuracao> consultarConfiguracoes() {
		return gestaoDeConfiguracao.consultarConfiguracoes();
	}

	/**
	 * Método que cria uma nova configuração.
	 *
	 */
	public void criarConfiguracao() {
		gestaoDeConfiguracao.criarConfiguracao();
	}

	/**
	 * Método que remove a configuração com o id passado como parâmetro
	 * do sistema.
	 *
	 * @param configuracaoId Id da configuração que se pretende remover
	 */
	public void removerConfiguracao(int configuracaoId) {
		gestaoDeConfiguracao.removerConfiguracao(configuracaoId);
	}
	/**
	 * Método que devolve os componentes dentro de um determinado pacote.
	 *
	 * @param pacote_id Id do pacote em questão
	 * @return List<Componente> Lista dos componentes no pacote
	 */
	public List<Componente> consultarComponentesNoPacote(int pacote_id) {

		Pacote p = pacoteDAO.get(pacote_id);
		return new ArrayList<>(p.getComponentes().values());
	}

	/**
	 * Método que remove um determinado componente de um determinado pacote.
	 *
	 * @param pacote Pacote em questão
	 * @param componente_id Id do componente que se pretende eliminar do pacote
	 */
	public void removerComponenteDoPacote(Pacote pacote, int componente_id) {
		pacote.removeComponente(componente_id);
	}

	/**
	 * Método que gera uma configuração ótima, ou seja, uma configuração que tenta
	 * maximizar a utilização do dinheiro previsto.
	 *

	 * configuração ótima

	 * @param valor Valor máximo que o cliente está disposto a gastar
	 * @return Configuracao ótima gerada
	 * @throws NoOptimalConfigurationException Caso não exista nenhuma configuração
	 * ótima tendo em consideração os parâmetros fornecidos
	 * @throws ConfiguracaoNaoTemObrigatoriosException Caso a configuração não
	 * contenha os componentes básicos (obrigatórios)
	 */

	public Configuracao ConfiguracaoOtima(double valor) throws NoOptimalConfigurationException, ConfiguracaoNaoTemObrigatoriosException {
		Collection<RemoteClass<Integer>> pacsIds = pacoteDAO.values();
		Collection<RemoteClass<Integer>> compsIds = componenteDAO.values();

		ArrayList<Pacote> pacs = new ArrayList<>();
		pacsIds.forEach(id -> pacs.add(pacoteDAO.get(id.key())));

		ArrayList<Componente> comps = new ArrayList<>();
		compsIds.forEach(id -> comps.add(componenteDAO.get(id.key())));

		return gestaoDeConfiguracao.configuracaoOtima(comps,pacs,valor);
	}

	/**
	 * Método que adiciona um determinado componente a um determinado pacote.
	 *
	 * @param pacote Pacote em questão
	 * @param componente_id Id do componente que se pretende adicionar ao pacote
	 * @throws ComponenteJaExisteNoPacoteException Caso o componente que se
	 * pretende adicionar já esteja presente no pacote
	 * @throws ComponenteIncompativelNoPacoteException Caso o pacote contenha um
	 * componente incompatível com o componente que se pretende adicioanr
	 */
	public void adicionarComponenteAoPacote(Pacote pacote, int componente_id) throws ComponenteJaExisteNoPacoteException, ComponenteIncompativelNoPacoteException {
		pacote.adicionaComponente(componente_id);
	}

	/**
	 * Método que cria uma nova encomenda no sistema.
	 *

	 * @param nomeCliente Nome do cliente a que a encomenda corresponde
	 * @param numeroDeIdentificacaoCliente Número de Identificação do cliente
	 * @param moradaCliente Morada do cliente
	 * @param paisCliente País do cliente
	 * @param emailCliente E-mail do cliente
	 * @throws EncomendaTemComponentesIncompativeis Se a configuração tem componentes
	 * incompatíveis
	 * @throws EncomendaRequerOutrosComponentes Se existem componentes na configuração
	 * que requerem outros componentes que não estão presentes na mesma
	 * @throws EncomendaRequerObrigatoriosException Se a configuração não tem todos
	 * os componentes obrigatórios
	 */
	public void criarEncomenda(
			String nomeCliente,
			String numeroDeIdentificacaoCliente,
			String moradaCliente,
			String paisCliente,
			String emailCliente
	) throws EncomendaRequerOutrosComponentes, EncomendaTemComponentesIncompativeis, EncomendaRequerObrigatoriosException {
		gestaoDeConfiguracao.criarEncomenda(nomeCliente, numeroDeIdentificacaoCliente, moradaCliente,
											  paisCliente, emailCliente);
	}

	/**
	 * Devolve a próxima encomenda que está pronta a ser produzida (existindo
	 * em stock todos os componentes necessários para a produção da encomenda).
	 *
	 * @return Encomenda pronta a produzir
	 * @throws SemEncomendasDisponiveisException Caso não exista nenhuma encomenda disponível,
	 * por exemplo, se simplesmente não existir nenhuma encomenda para produzir ou
	 * se não houver em stock algum dos componentes necessários para a encomenda
	 */
	public Encomenda consultarProximaEncomenda() throws SemEncomendasDisponiveisException {
		return operacaoFabril.consultarProximaEncomenda();
	}

	/**
	 * Método que remove uma encomenda (incluindo da base de dados).
	 *
	 * @param id Id da encomenda que se pretende remover
	 */
	public void removerEncomenda(Integer id) {
		operacaoFabril.removerEncomenda(id);
	}

	/**
	 * Atualiza o stock de um determinado componente (incluindo na base de dados),
	 * por exemplo, caso algum componente se parta ou esteja esquecido na Fábrica.
	 *
	 * @param componente Objeto componente já com as informações novas
	 * @throws SemEncomendasDisponiveisException Caso esta atualização não altere
	 * as encomendas disponíveis a produzir.
	 */
	public Encomenda atualizarStock(Componente componente) throws SemEncomendasDisponiveisException, StockInvalidoException {
		return operacaoFabril.atualizarStock(componente);
	}

	public boolean checkforPacotesInConfiguration() {
		return gestaoDeConfiguracao.checkforPacotesInConfiguration();
	}

	public void adicionarComponente(int id) throws ComponenteJaAdicionadoException{
		gestaoDeConfiguracao.adicionarComponente(id);
	}

	public List<Componente> componentesIncompativeisNaConfig(Map<Integer, Componente> comps) {
		return this.gestaoDeConfiguracao.componentesIncompativeisNaConfig(comps);
	}

	public List<Componente> componentesRequeridosQueNaoEstaoConfig(Map<Integer, Componente> comps) {
		return gestaoDeConfiguracao.componentesRequeridosQueNaoEstaoConfig(comps);
	}

	public Pacote adicionarPacote(int id, Pacote p) throws PacoteJaAdicionadoException{
		return gestaoDeConfiguracao.adicionarPacote(id, p);
	}

	public Configuracao getConfigAtual() {
		return gestaoDeConfiguracao.getConfigAtual();
	}

	public List<Componente> componentesRequeremMeNaConfig(int id) {
		return gestaoDeConfiguracao.componentesRequeremMeNaConfig(id);
	}

	public void removerComponente(int id) {
		gestaoDeConfiguracao.removerComponente(id);
	}

	public void removerPacoteConfig(int id) {
		gestaoDeConfiguracao.removerPacoteConfig(id);
	}

	public void loadConfigAtual(int id) {
		gestaoDeConfiguracao.loadConfigAtual(id);
	}

	public void atualizarUser(Utilizador u){
		this.utilizadorDAO.update(u.key(),u);
	}

	public void atualizarDesconto(Pacote p){
		this.pacoteDAO.updateDesconto(p);
	}

}

