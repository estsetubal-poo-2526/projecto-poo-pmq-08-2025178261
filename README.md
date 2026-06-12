# Batalha Naval — Projeto POO 2025/2026

**Unidade Curricular:** Programação Orientada a Objetos  
**Docente:** Pedro Mesquita | **Turma:** PL1 — ESTSetúbal/IPS  
**Ano Letivo:** 2025/2026  
**Grupo:** Afonso Ernesto (nº 2025178261) · Rodrigo Saldanha (nº 2024130457)

---

## Descrição

Jogo Batalha Naval em Java com interface gráfica JavaFX.  
O jogador coloca os seus navios no tabuleiro e tenta afundar todos os navios da CPU antes que ela afunde os seus.

---

## Como Executar

1. Abrir o projeto no **IntelliJ IDEA**
2. Aguardar o Maven descarregar as dependências (popup no canto superior direito)
3. Abrir o ficheiro `src/main/java/batalhanaval/MainApp.java`
4. Clicar no botão **▶** para executar

---

## Como Executar os Testes

No IntelliJ, clica com o botão direito em `src/test/java` → **Run All Tests**

---

## Estrutura do Projeto
src/

├── main/java/batalhanaval/         
│   ├── MainApp.java   
│   ├── controller/GameController.java   
│   ├── model/         (Game, Jogador, Player, CPU, Tabuleiro, Navio, ...)              
│   ├── view/          (MenuView, ColocacaoView, GameView, ResultadoView, ...)  
│   └── exception/     (PosicaoInvalidaException, ColocacaoNavioException)  
└── test/java/batalhanaval/     
├── PosicaoTest.java    
├── NavioTest.java  
├── TabuleiroTest.java  
└── GameTest.java

## Regras do Jogo

1. Cada jogador tem 5 navios — Porta-Aviões (5), Navio de Guerra (4), Submarino (3), Contratorpedeiro (3) e Lancha (2)
2. Os navios podem ser colocados na horizontal ou vertical
3. As jogadas são alternadas: jogador → CPU → jogador
4. Vence quem afundar todos os navios do adversário primeiro