package io.github.teste;

public enum WeaponType {

    DEFAULT(0.2f, 0f, 1),

    BURST(0.17f, 0.05f, 3);

    // Cooldown total entre uma rajada/tiro e outro
    public final float cooldown;

    // Intervalo de tempo entre cada bala dentro de uma rajada
    // (irrelevante para DEFAULT, pois só dispara 1 bala)
    public final float burstInterval;

    // Quantidade de balas disparadas por acionamento
    public final int bulletCount;

    WeaponType(float cooldown, float burstInterval, int bulletCount) {
        this.cooldown = cooldown;
        this.burstInterval = burstInterval;
        this.bulletCount = bulletCount;
    }
}
