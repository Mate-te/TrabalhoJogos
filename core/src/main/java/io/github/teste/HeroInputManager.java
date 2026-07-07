package io.github.teste;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class HeroInputManager implements InputProcessor {
    private Hero hero;
    private World world;
    private Texture heroTexture;

    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean moveUp = false;
    private boolean moveDown = false;

    private float zoomLevel = 1f;
    private final float MIN_ZOOM = 0.5f;
    private final float MAX_ZOOM = 1f;
    private final float ZOOM_SPEED = 0.1f;

    private float shootCooldownTimer = 0f; // Tempo restante até poder atirar novamente
    private boolean isBursting = false;    // Se uma rajada está em andamento
    private int bulletsInBurst = 0;        // Quantas balas já foram disparadas na rajada atual
    private float burstTimer = 0f;         // Timer entre cada bala do burst
    private float lastShotAngle = 0f;

    public HeroInputManager(Hero hero, World world, Texture heroTexture) {
        this.hero = hero;
        this.world = world;
        this.heroTexture = heroTexture;
    }

    public void update(float delta) {
        // Decrementa o cooldown entre rajadas
        if (shootCooldownTimer > 0) {
            shootCooldownTimer -= delta;
        }
        // Processa as balas restantes de uma rajada de burst em andamento
        if (isBursting) {
            burstTimer -= delta;
            if (burstTimer <= 0) {
                WeaponType weapon = hero.getWeapon();

                // Dispara a próxima bala da rajada
                float shootX = hero.getX() + hero.getWidth() / 2f;
                float shootY = hero.getY() + hero.getHeight() / 2f;
                world.shootBurst(shootX, shootY, lastShotAngle);
                bulletsInBurst++;
                if (bulletsInBurst >= weapon.bulletCount) {
                    // Rajada completa — inicia cooldown e encerra o estado de burst
                    isBursting = false;
                    shootCooldownTimer = weapon.cooldown;
                } else {
                    // Ainda há balas — reinicia o timer para a próxima bala
                    burstTimer = weapon.burstInterval;
                }
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            moveLeft = true;
            updateHeroVelocity();
            return true;
        }
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            moveRight = true;
            updateHeroVelocity();
            return true;
        }
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            moveUp = true;
            updateHeroVelocity();
            return true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            moveDown = true;
            updateHeroVelocity();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
            moveLeft = false;
            updateHeroVelocity();
            return true;
        }
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            moveRight = false;
            updateHeroVelocity();
            return true;
        }
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) {
            moveUp = false;
            updateHeroVelocity();
            return true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            moveDown = false;
            updateHeroVelocity();
            return true;
        }
        return false;
    }

    private void updateHeroVelocity() {
        float vx = 0f;
        float vy = 0f;

        if (moveLeft) vx -= hero.getMoveSpeed();
        if (moveRight) vx += hero.getMoveSpeed();
        if (moveUp) vy += hero.getMoveSpeed();
        if (moveDown) vy -= hero.getMoveSpeed();

        hero.setVelocityX(vx);
        hero.setVelocityY(vy);

        // Troca o sprite para dar impressão de movimento quando o herói está se movendo
        if (vx != 0f || vy != 0f) {
            // Use a textura "NAVELFOGO" enquanto se move
            hero.setTexture(Assets.manager.get(Assets.NAVELFOGO, com.badlogic.gdx.graphics.Texture.class));
        } else {
            // Texto estática quando parado
            hero.setTexture(Assets.manager.get(Assets.NAVE, com.badlogic.gdx.graphics.Texture.class));
        }
    }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && hero != null && hero.isAlive() && world != null) {
            if (shootCooldownTimer > 0 || isBursting) return true;
            WeaponType weapon = hero.getWeapon();
            float shootX = hero.getX() + hero.getWidth() / 2f;
            float shootY = hero.getY() + hero.getHeight() / 2f;
            float angle = hero.getRotation();

            // Dispara a primeira bala imediatamente em qualquer arma
            if (weapon == WeaponType.DEFAULT) {
                world.shoot(shootX, shootY, angle);
                shootCooldownTimer = weapon.cooldown;
            } else if (weapon == WeaponType.BURST) {
                world.shootBurst(shootX, shootY, angle); // 1ª bala com textura burst
                isBursting = true;
                bulletsInBurst = 1;
                lastShotAngle = angle;
                burstTimer = weapon.burstInterval;
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY > 0) {
            zoomLevel -= ZOOM_SPEED;
        } else if (amountY < 0) {
            zoomLevel += ZOOM_SPEED;
        }

        zoomLevel = MathUtils.clamp(zoomLevel, MIN_ZOOM, MAX_ZOOM);

        return true;
    }

    public float getZoomLevel() {
        return zoomLevel;
    }
}
