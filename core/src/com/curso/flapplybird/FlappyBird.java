package com.curso.flapplybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;

	//configurações
	private float variacao = 0;
	private float alturaTela;
	private float larguraTela;
	private float posicaoInicial = 0;
	private float velocidadeQueda = 0;
	private float posicaoMovimentoCanoHorizontal;
	private float larguraEntreCanos;
	private float deltaTime;
	private Random numerosRandomicos;
	private float alturaEntreCanosRandomica;

	private int estadoJogo = 0;
	private BitmapFont fonte;
	private int pontuacao = 0;
	private boolean marcouPonto;
	private BitmapFont mensagemReiniciar;

	private Circle passaroCirculo;
	private Rectangle retanguloCanoBaixo;
	private Rectangle retanguloCanoAlto;
	//private ShapeRenderer shape;

	//camera
	private OrthographicCamera camera;
	private Viewport viewPort;
	private static final float VIRTUAL_WIDTH = 765;
	private static final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {
		batch = new SpriteBatch();

		fundo = new Texture("fundo.png");
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		gameOver = new Texture("game_over.png");

		//shape = new ShapeRenderer();
		passaroCirculo = new Circle();

		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(4);

		mensagemReiniciar = new BitmapFont();
		mensagemReiniciar.setColor(Color.WHITE);
		mensagemReiniciar.getData().setScale(3);

		larguraTela = VIRTUAL_WIDTH;
		alturaTela = VIRTUAL_HEIGHT;

		//configurações da camera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		viewPort = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		numerosRandomicos = new Random();

		posicaoMovimentoCanoHorizontal = larguraTela;
		larguraEntreCanos = 300;

		posicaoInicial = alturaTela / 2;
	}

	@Override
	public void render () {

		camera.update();

		//limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;

		if(variacao > 2) variacao = 0;

		if(estadoJogo == 0) {

			if(Gdx.input.justTouched()) {
				estadoJogo = 1;
			}

		} else {


			velocidadeQueda++;
			if(posicaoInicial > 0 || velocidadeQueda < 0)
				posicaoInicial -= velocidadeQueda;

			if(estadoJogo == 1) {
				posicaoMovimentoCanoHorizontal -= deltaTime * 200;

				if(Gdx.input.justTouched()) {
					velocidadeQueda = -17;
				}

				if(posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraTela;
					alturaEntreCanosRandomica = numerosRandomicos.nextInt(400) - 200;
					marcouPonto = false;
				}

				//verifica pontuação
				if(posicaoMovimentoCanoHorizontal < 120) {

					if(!marcouPonto) {
						pontuacao++;
						marcouPonto = true;
					}
				}
			} else {

				if(Gdx.input.justTouched()) {
					estadoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicial = alturaTela / 2;
					posicaoMovimentoCanoHorizontal = larguraTela;
				}

			}

		}

		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		batch.draw(fundo, 0, 0, larguraTela, alturaTela);
		batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaTela / 2 + larguraEntreCanos / 2 + alturaEntreCanosRandomica);
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaTela / 2 - canoBaixo.getHeight() - larguraEntreCanos / 2 + alturaEntreCanosRandomica);
		batch.draw(passaros[ (int) variacao], 120, posicaoInicial);
		fonte.draw(batch, String.valueOf(pontuacao), larguraTela / 2, alturaTela - 50);

		if(estadoJogo == 2) {
			batch.draw(gameOver, larguraTela / 2 - gameOver.getWidth() / 2, alturaTela / 2);
			mensagemReiniciar.draw(batch, "Toque na tela para reiniciar", larguraTela / 2 - 260, alturaTela / 2 - 100);
		}

		batch.end();

		passaroCirculo.set(120 + passaros[0].getWidth() / 2, posicaoInicial + passaros[0].getHeight() / 2, passaros[0].getHeight() / 2);

		retanguloCanoBaixo = new Rectangle(posicaoMovimentoCanoHorizontal, alturaTela / 2 - canoBaixo.getHeight() - larguraEntreCanos / 2 + alturaEntreCanosRandomica,
				canoBaixo.getWidth(), canoBaixo.getHeight());

		retanguloCanoAlto = new Rectangle(posicaoMovimentoCanoHorizontal, alturaTela / 2 + larguraEntreCanos / 2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(), canoTopo.getHeight());

		/*
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
		shape.setColor(Color.RED);

		shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
		shape.setColor(Color.RED);

		shape.rect(retanguloCanoAlto.x, retanguloCanoAlto.y, retanguloCanoAlto.width, retanguloCanoAlto.height);
		shape.setColor(Color.RED);

		shape.end();
		*/

		if(Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo,
				retanguloCanoAlto) || posicaoInicial <= 0 || posicaoInicial >= alturaTela) {
			estadoJogo = 2;
		}
	}

	@Override
	public void resize(int width, int height) {
		viewPort.update(width, height);
	}
}
