package main.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.model.Index;
import main.model.Lemma;
import main.model.Page;
import main.model.Site;
import main.model.StatusType;

public interface ManagerService {

  /**
   * добавить страницу в БД
   *
   * @param page страница
   */
  void addPage(Page page);

  /**
   *вернуть адреса страниц
   *
   * @return список адресов страниц
   */
  List<String> getPathPages();

  /**
   *
   * @param lemma
   * @param frequency
   * @param siteId
   * @return
   */
  int addLemma(String lemma, int frequency, int siteId);

  /**
   *
   * @param lemma
   * @param frequency
   * @return
   */
  int updateLemma(Lemma lemma, int frequency);

  /**
   * добавить индекс
   *
   * @param page страница
   * @param lemma лемма
   * @param rank ранг леммы на странице
   */
  void addIndex(Page page, Lemma lemma, float rank);

  /**
   * обновить индекс
   *
   * @param index индекс
   * @param rank новое значение ранга
   */
  void updateIndex(Index index, float rank);

  /**
   *
   * @param site сайт
   */
  void addSite(Site site);

  /**
   *
   * @param site
   */
  int updateSite(Site site, StatusType statusType, Date date);

  /**
   *
    * @param listSites Map с индексированными сайтами
   */
  void updateStatusSite(Map<String, String> listSites);

  /**
   * удаление всех записей
   */
  void deleteAll();
}
