import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [cities, setCities] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [sort, setSort] = useState('');
  const [filter, setFilter] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [showGenocideForm, setShowGenocideForm] = useState(false);
  const [auth, setAuth] = useState({ username: '', password: '' });
  const [showAuthForm, setShowAuthForm] = useState(true);

  // Форма создания города
  const [cityForm, setCityForm] = useState({
    name: '',
    coordinates: { x: '', y: '' },
    area: '',
    population: '',
    metersAboveSeaLevel: '',
    carCode: '',
    climate: 'OCEANIC',
    standardOfLiving: 'MEDIUM',
    governor: { name: '', age: '', height: '' }
  });

  // Форма геноцида
  const [genocideForm, setGenocideForm] = useState({
    id1: '',
    id2: '',
    id3: '',
    deportFrom: '',
    deportTo: ''
  });

  const API_BASE_URL = 'https://localhost:5736/api/v1';
  const GENOCIDE_API_URL = 'https://localhost:5737/api/v1/genocide';

  // Создание заголовков с аутентификацией
  const getAuthHeaders = () => {
    if (!auth.username || !auth.password) {
      return {};
    }

    const token = btoa(`${auth.username}:${auth.password}`);
    return {
      'Authorization': `Basic ${token}`,
      'Accept': 'application/xml'
    };
  };

  // Парсер XML для браузера
  const parseXML = (xmlString) => {
    return new Promise((resolve, reject) => {
      try {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "text/xml");

        // Проверяем на ошибки парсинга
        const parserError = xmlDoc.querySelector('parsererror');
        if (parserError) {
          reject(new Error('XML parsing error'));
          return;
        }

        resolve(xmlDoc);
      } catch (err) {
        reject(err);
      }
    });
  };

  // Получение списка городов
  const fetchCities = async () => {
    if (!auth.username || !auth.password) {
      setError('Пожалуйста, введите данные для аутентификации');
      setShowAuthForm(true);
      return;
    }

    setLoading(true);
    setError('');
    try {
      const params = new URLSearchParams();
      if (page) params.append('page', page);
      if (pageSize) params.append('pageSize', pageSize);
      if (sort) params.append('sort', sort);
      if (filter) params.append('filter', filter);

      const url = `${API_BASE_URL}/cities${params.toString() ? `?${params.toString()}` : ''}`;

      const response = await axios.get(url, {
        headers: getAuthHeaders()
      });

      const xmlDoc = await parseXML(response.data);

      // Парсим данные городов
      const citiesData = [];
      const dataElement = xmlDoc.getElementsByTagName('data')[0];
      if (dataElement) {
        const citiesElements = dataElement.getElementsByTagName('City');

        for (let i = 0; i < citiesElements.length; i++) {
          const cityElement = citiesElements[i];
          const city = {};

          // Парсим простые поля
          const idElement = cityElement.getElementsByTagName('id')[0];
          if (idElement) city.id = parseInt(idElement.textContent);

          const nameElement = cityElement.getElementsByTagName('name')[0];
          if (nameElement) city.name = nameElement.textContent;

          const areaElement = cityElement.getElementsByTagName('area')[0];
          if (areaElement) city.area = parseInt(areaElement.textContent);

          const populationElement = cityElement.getElementsByTagName('population')[0];
          if (populationElement) city.population = parseInt(populationElement.textContent);

          const climateElement = cityElement.getElementsByTagName('climate')[0];
          if (climateElement) city.climate = climateElement.textContent;

          const carCodeElement = cityElement.getElementsByTagName('carCode')[0];
          if (carCodeElement) city.carCode = parseInt(carCodeElement.textContent);

          // Парсим координаты
          const coordinatesElement = cityElement.getElementsByTagName('coordinates')[0];
          if (coordinatesElement) {
            const xElement = coordinatesElement.getElementsByTagName('x')[0];
            const yElement = coordinatesElement.getElementsByTagName('y')[0];
            city.coordinates = {
              x: xElement ? parseFloat(xElement.textContent) : 0,
              y: yElement ? parseFloat(yElement.textContent) : 0
            };
          }

          // Парсим губернатора
          const governorElement = cityElement.getElementsByTagName('governor')[0];
          if (governorElement) {
            const governor = {};
            const govNameElement = governorElement.getElementsByTagName('name')[0];
            if (govNameElement) governor.name = govNameElement.textContent;

            const govAgeElement = governorElement.getElementsByTagName('age')[0];
            if (govAgeElement) governor.age = parseInt(govAgeElement.textContent);

            city.governor = governor;
          }

          citiesData.push(city);
        }
      }

      setCities(citiesData);

      // Получаем totalPages из ответа
      const totalPagesElement = xmlDoc.getElementsByTagName('totalPages')[0];
      if (totalPagesElement) {
        setTotalPages(parseInt(totalPagesElement.textContent) || 1);
      }

    } catch (err) {
      if (err.response?.status === 401) {
        setError('Неверные учетные данные. Пожалуйста, проверьте логин и пароль.');
        setShowAuthForm(true);
      } else {
        setError(`Ошибка загрузки городов: ${err.message}`);
      }
      console.error('Error fetching cities:', err);
    } finally {
      setLoading(false);
    }
  };

  // Создание города
  const createCity = async (cityData) => {
    try {
      const xmlData = `
        <CreateCityRequest>
          <name>${cityData.name}</name>
          <coordinates>
            <x>${cityData.coordinates.x}</x>
            <y>${cityData.coordinates.y}</y>
          </coordinates>
          <area>${cityData.area}</area>
          <population>${cityData.population}</population>
          ${cityData.metersAboveSeaLevel ? `<metersAboveSeaLevel>${cityData.metersAboveSeaLevel}</metersAboveSeaLevel>` : ''}
          <carCode>${cityData.carCode}</carCode>
          <climate>${cityData.climate}</climate>
          ${cityData.standardOfLiving ? `<standardOfLiving>${cityData.standardOfLiving}</standardOfLiving>` : ''}
          <governor>
            <name>${cityData.governor.name}</name>
            ${cityData.governor.age ? `<age>${cityData.governor.age}</age>` : ''}
            ${cityData.governor.height ? `<height>${cityData.governor.height}</height>` : ''}
          </governor>
        </CreateCityRequest>
      `;

      await axios.post(`${API_BASE_URL}/cities`, xmlData, {
        headers: {
          ...getAuthHeaders(),
          'Content-Type': 'application/xml'
        }
      });

      setShowCreateForm(false);
      resetForm();
      fetchCities();
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Неверные учетные данные. Пожалуйста, проверьте логин и пароль.');
        setShowAuthForm(true);
      } else {
        setError(`Ошибка создания города: ${err.message}`);
      }
    }
  };

  // Удаление города
  const deleteCity = async (id) => {
    if (window.confirm('Вы уверены, что хотите удалить этот город?')) {
      try {
        await axios.delete(`${API_BASE_URL}/cities/${id}`, {
          headers: getAuthHeaders()
        });
        fetchCities();
      } catch (err) {
        if (err.response?.status === 401) {
          setError('Неверные учетные данные. Пожалуйста, проверьте логин и пароль.');
          setShowAuthForm(true);
        } else {
          setError(`Ошибка удаления города: ${err.message}`);
        }
      }
    }
  };

  // Расчет суммы населения
  const calculateSum = async () => {
    try {
      const response = await axios.get(
        `${GENOCIDE_API_URL}/count/${genocideForm.id1}/${genocideForm.id2}/${genocideForm.id3}`,
        {
          headers: getAuthHeaders()
        }
      );

      // Парсим XML ответ
      const xmlDoc = await parseXML(response.data);
      const sumValue = xmlDoc.documentElement.textContent;

      alert(`Суммарное население: ${sumValue || 'Неизвестно'}`);
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Неверные учетные данные для сервиса геноцида.');
        setShowAuthForm(true);
      } else {
        setError(`Ошибка расчета суммы: ${err.message}`);
      }
    }
  };

  // Депортация населения
  const deportPopulation = async () => {
    try {
      const response = await axios.post(
        `${GENOCIDE_API_URL}/deport/${genocideForm.deportFrom}/${genocideForm.deportTo}`,
        {},
        {
          headers: getAuthHeaders()
        }
      );

      // Парсим XML ответ
      const xmlDoc = await parseXML(response.data);
      const movedValue = xmlDoc.documentElement.textContent;

      alert(`Перемещено населения: ${movedValue || 'Неизвестно'}`);
      fetchCities();
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Неверные учетные данные для сервиса геноцида.');
        setShowAuthForm(true);
      } else {
        setError(`Ошибка депортации: ${err.message}`);
      }
    }
  };

  const resetForm = () => {
    setCityForm({
      name: '',
      coordinates: { x: '', y: '' },
      area: '',
      population: '',
      metersAboveSeaLevel: '',
      carCode: '',
      climate: 'OCEANIC',
      standardOfLiving: 'MEDIUM',
      governor: { name: '', age: '', height: '' }
    });
  };

  const handleAuthSubmit = (e) => {
    e.preventDefault();
    setShowAuthForm(false);
    fetchCities();
  };

  useEffect(() => {
    if (!showAuthForm && auth.username && auth.password) {
      fetchCities();
    }
  }, [page, pageSize, sort, filter, showAuthForm, auth]);

  if (showAuthForm) {
    return (
      <div className="App">
        <header className="App-header">
          <h1>Городской сервис управления</h1>
        </header>

        <main className="main-content">
          <div className="auth-form">
            <h2>Аутентификация</h2>
            <form onSubmit={handleAuthSubmit}>
              <div className="form-group">
                <label>Имя пользователя:</label>
                <input
                  type="text"
                  value={auth.username}
                  onChange={(e) => setAuth({...auth, username: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>Пароль:</label>
                <input
                  type="password"
                  value={auth.password}
                  onChange={(e) => setAuth({...auth, password: e.target.value})}
                  required
                />
              </div>
              {error && <div className="error-message">{error}</div>}
              <button type="submit">Войти</button>
            </form>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>Городской сервис управления</h1>
        <div className="user-info">
          Пользователь: {auth.username}
          <button onClick={() => setShowAuthForm(true)} className="logout-btn">
            Сменить пользователя
          </button>
        </div>
      </header>

      <main className="main-content">
        {error && <div className="error-message">{error}</div>}

        {/* Панель управления */}
        <div className="controls">
          <button onClick={() => setShowCreateForm(!showCreateForm)}>
            {showCreateForm ? 'Отмена' : 'Добавить город'}
          </button>

          <button onClick={() => setShowGenocideForm(!showGenocideForm)}>
            {showGenocideForm ? 'Скрыть геноцид' : 'Геноцид'}
          </button>

          <div className="filters">
            <input
              type="text"
              placeholder="Сортировка (например: name,-population)"
              value={sort}
              onChange={(e) => setSort(e.target.value)}
            />
            <input
              type="text"
              placeholder="Фильтр (например: name[eq]=Moscow)"
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
            />
          </div>
        </div>

        {/* Форма создания города */}
        {showCreateForm && (
          <div className="form-container">
            <h2>Добавить новый город</h2>
            <form onSubmit={(e) => {
              e.preventDefault();
              createCity(cityForm);
            }}>
              <div className="form-grid">
                <input
                  type="text"
                  placeholder="Название города"
                  value={cityForm.name}
                  onChange={(e) => setCityForm({...cityForm, name: e.target.value})}
                  required
                />
                <input
                  type="number"
                  placeholder="Площадь"
                  value={cityForm.area}
                  onChange={(e) => setCityForm({...cityForm, area: e.target.value})}
                  required
                />
                <input
                  type="number"
                  placeholder="Население"
                  value={cityForm.population}
                  onChange={(e) => setCityForm({...cityForm, population: e.target.value})}
                  required
                />
                <input
                  type="number"
                  placeholder="Код автомобиля"
                  value={cityForm.carCode}
                  onChange={(e) => setCityForm({...cityForm, carCode: e.target.value})}
                  required
                />
                <input
                  type="number"
                  step="0.01"
                  placeholder="Координата X"
                  value={cityForm.coordinates.x}
                  onChange={(e) => setCityForm({
                    ...cityForm,
                    coordinates: {...cityForm.coordinates, x: e.target.value}
                  })}
                  required
                />
                <input
                  type="number"
                  step="0.01"
                  placeholder="Координата Y"
                  value={cityForm.coordinates.y}
                  onChange={(e) => setCityForm({
                    ...cityForm,
                    coordinates: {...cityForm.coordinates, y: e.target.value}
                  })}
                  required
                />
                <input
                  type="number"
                  step="0.01"
                  placeholder="Высота над уровнем моря (опционально)"
                  value={cityForm.metersAboveSeaLevel}
                  onChange={(e) => setCityForm({...cityForm, metersAboveSeaLevel: e.target.value})}
                />

                <select
                  value={cityForm.climate}
                  onChange={(e) => setCityForm({...cityForm, climate: e.target.value})}
                >
                  <option value="OCEANIC">Океанический</option>
                  <option value="STEPPE">Степной</option>
                  <option value="SUBARCTIC">Субарктический</option>
                  <option value="DESERT">Пустынный</option>
                </select>

                <select
                  value={cityForm.standardOfLiving}
                  onChange={(e) => setCityForm({...cityForm, standardOfLiving: e.target.value})}
                >
                  <option value="">Не выбрано</option>
                  <option value="ULTRA_HIGH">Очень высокий</option>
                  <option value="MEDIUM">Средний</option>
                  <option value="VERY_LOW">Очень низкий</option>
                </select>
              </div>

              <h3>Губернатор</h3>
              <div className="form-grid">
                <input
                  type="text"
                  placeholder="Имя губернатора"
                  value={cityForm.governor.name}
                  onChange={(e) => setCityForm({
                    ...cityForm,
                    governor: {...cityForm.governor, name: e.target.value}
                  })}
                  required
                />
                <input
                  type="number"
                  placeholder="Возраст (опционально)"
                  value={cityForm.governor.age}
                  onChange={(e) => setCityForm({
                    ...cityForm,
                    governor: {...cityForm.governor, age: e.target.value}
                  })}
                />
                <input
                  type="number"
                  step="0.01"
                  placeholder="Рост (опционально)"
                  value={cityForm.governor.height}
                  onChange={(e) => setCityForm({
                    ...cityForm,
                    governor: {...cityForm.governor, height: e.target.value}
                  })}
                />
              </div>

              <button type="submit">Создать город</button>
            </form>
          </div>
        )}

        {/* Форма геноцида */}
        {showGenocideForm && (
          <div className="form-container">
            <h2>Геноцидные операции</h2>

            <div className="genocide-section">
              <h3>Рассчитать сумму населения</h3>
              <div className="form-row">
                <input
                  type="number"
                  placeholder="ID города 1"
                  value={genocideForm.id1}
                  onChange={(e) => setGenocideForm({...genocideForm, id1: e.target.value})}
                />
                <input
                  type="number"
                  placeholder="ID города 2"
                  value={genocideForm.id2}
                  onChange={(e) => setGenocideForm({...genocideForm, id2: e.target.value})}
                />
                <input
                  type="number"
                  placeholder="ID города 3"
                  value={genocideForm.id3}
                  onChange={(e) => setGenocideForm({...genocideForm, id3: e.target.value})}
                />
                <button onClick={calculateSum}>Рассчитать</button>
              </div>
            </div>

            <div className="genocide-section">
              <h3>Депортировать население</h3>
              <div className="form-row">
                <input
                  type="number"
                  placeholder="ID города-источника"
                  value={genocideForm.deportFrom}
                  onChange={(e) => setGenocideForm({...genocideForm, deportFrom: e.target.value})}
                />
                <input
                  type="number"
                  placeholder="ID города-назначения"
                  value={genocideForm.deportTo}
                  onChange={(e) => setGenocideForm({...genocideForm, deportTo: e.target.value})}
                />
                <button onClick={deportPopulation}>Депортировать</button>
              </div>
            </div>
          </div>
        )}

        {/* Таблица городов */}
        <div className="table-container">
          {loading ? (
            <div>Загрузка...</div>
          ) : (
            <>
              <table className="cities-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Название</th>
                    <th>Координаты</th>
                    <th>Площадь</th>
                    <th>Население</th>
                    <th>Климат</th>
                    <th>Губернатор</th>
                    <th>Действия</th>
                  </tr>
                </thead>
                <tbody>
                  {cities.map((city) => (
                    <tr key={city.id}>
                      <td>{city.id}</td>
                      <td>{city.name}</td>
                      <td>({city.coordinates?.x}, {city.coordinates?.y})</td>
                      <td>{city.area}</td>
                      <td>{city.population}</td>
                      <td>{city.climate}</td>
                      <td>
                        {city.governor?.name}
                        {city.governor?.age && ` (${city.governor.age} лет)`}
                      </td>
                      <td>
                        <button
                          onClick={() => deleteCity(city.id)}
                          className="delete-btn"
                        >
                          Удалить
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {/* Пагинация */}
              <div className="pagination">
                <button
                  onClick={() => setPage(p => Math.max(1, p - 1))}
                  disabled={page <= 1}
                >
                  Назад
                </button>
                <span>Страница {page} из {totalPages}</span>
                <button
                  onClick={() => setPage(p => p + 1)}
                  disabled={page >= totalPages}
                >
                  Вперед
                </button>
              </div>
            </>
          )}
        </div>
      </main>
    </div>
  );
}

export default App;
