package ua.krainov.synthesis.api;

/**
 * Джерело обертання поруч із машиною (без залежності від Create).
 * Реалізує Кінетичний привід з compat/create, якщо Create встановлено.
 */
public interface RotationSource {
    /** Поточна швидкість у RPM (0, якщо мережа перевантажена). */
    int getRpm();

    /** Скільки навантаження (SU) машина зараз бере з мережі. 0 — не працює. */
    void setRequiredStress(int stressUnits);
}
