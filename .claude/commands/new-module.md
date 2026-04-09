# Создать новый модуль

Создай новый interview prep модуль в `modules/$ARGUMENTS`.

## Шаги

1. **Уточни у пользователя:**
   - Название модуля (папка в `modules/`)
   - Тема (краткое описание)
   - Тип сборки: `kotlin` | `java-plain` | `java-spring` | `no-build`
   - Список тем теории (файлы в `theory/`)

2. **Создай структуру директорий:**

   Для `kotlin` или `java-plain`:
   ```
   modules/<name>/
   ├── theory/
   ├── pom.xml
   └── src/
       ├── main/
       │   └── kotlin/exercises/   (или java/by/pavel/)
       └── test/
   ```

   Для `java-spring`:
   ```
   modules/<name>/
   ├── theory/
   ├── pom.xml
   ├── Dockerfile
   └── src/
       └── main/
           ├── java/by/pavel/
           └── resources/application.properties
   ```

   Для `no-build`:
   ```
   modules/<name>/
   ├── theory/
   └── exercises/
   ```

3. **Создай `pom.xml`** соответствующего типа (скопируй из аналогичного модуля и адаптируй `<artifactId>`).

4. **Создай `PROGRESS.md`** по шаблону:
   ```markdown
   # Progress Tracker — <ModuleName>

   ## Статус модулей

   | Модуль | Статус | Дата начала | Дата завершения |
   |--------|--------|-------------|-----------------|
   | 1. ... | ⬜ не начат | — | — |

   ## Упражнения / Теория

   | # | Тема | Статус |
   |---|------|--------|
   | ... | ... | ⬜ |

   ---
   Легенда: ⬜ не начато | 🔄 в процессе | ✅ завершено
   ```

5. **Создай `ROADMAP.md`** по шаблону:
   ```markdown
   # <ModuleName> — Roadmap

   ## Порядок прохождения

   | Приоритет | Модуль | Частота на собесах |
   |-----------|--------|--------------------|
   | 1 | ... | ★★★★★ |

   ---

   ## Модуль 1: ...

   📖 Теория: [theory/...](theory/...)

   - [ ] Тема 1
   - [ ] Тема 2

   **Упражнения:**
   - [ ] [ExXX: ...](src/...)
   ```

6. **Создай `INTERVIEW_QUESTIONS.md`** — минимум 15 вопросов с ответами по теме модуля.

7. **Создай `README.md`** с описанием модуля, структурой директорий, командами запуска.

8. **Добавь модуль в `CLAUDE.md`** (корневой):
   - В таблицу модулей (раздел "Структура")
   - В таблицу теории (раздел "Правило теории — NO OVERLAP") с указанием тем

## Правило теории

Перед добавлением теории — проверь существующие модули на пересечения:
- Темы concurrency → `modules/concurrency/`
- Темы database/distributed/microservices → `modules/system-design/`
- Темы infra/devops → `modules/infrastructure/`

Если тема уже покрыта в другом модуле — дай ссылку вместо дублирования.

## Пример

```
/new-module algorithms
```
→ Создаст `modules/algorithms/` с типом `kotlin`, теорией по алгоритмам, шаблонами PROGRESS/ROADMAP/INTERVIEW_QUESTIONS.
