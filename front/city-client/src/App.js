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

  const API_BASE_URL = 'https://127.0.0.1:8443/api/v1';
  const GENOCIDE_API_URL = 'https://127.0.0.1:8443/api/v1/genocide';

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


const parseXML = (xmlString) => {
  return new Promise((resolve, reject) => {
    try {
      const cleanXml = xmlString.trim();
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(cleanXml, "text/xml");

      const parserError = xmlDoc.querySelector('parsererror');
      if (parserError) {
        console.error('XML Parse Error:', parserError.textContent);
        reject(new Error('XML parsing error: ' + parserError.textContent));
        return;
      }

      resolve(xmlDoc);
    } catch (err) {
      reject(err);
    }
  });
};

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

    console.log('Raw XML Response:', response.data);

    const xmlDoc = await parseXML(response.data);

    // Парсим данные городов
    const citiesData = [];

    // Ищем корневой элемент CitiesResponseDto
    const rootElement = xmlDoc.documentElement;

    // Ищем data элемент (который содержит массив городов)
    let dataContainer = null;
    for (let i = 0; i < rootElement.children.length; i++) {
      if (rootElement.children[i].tagName === 'data') {
        dataContainer = rootElement.children[i];
        break;
      }
    }

    // Парсим города из data контейнера
    if (dataContainer) {
      for (let i = 0; i < dataContainer.children.length; i++) {
        const cityElement = dataContainer.children[i];
        if (cityElement.tagName === 'data') { // Города обернуты в <data>
          const city = parseCityElement(cityElement);
          if (city.id) {
            citiesData.push(city);
          }
        }
      }
    }

    console.log('Parsed cities:', citiesData);

    setCities(citiesData);

    // Получаем totalPages
    let totalPagesValue = 1;
    for (let i = 0; i < rootElement.children.length; i++) {
      const child = rootElement.children[i];
      if (child.tagName === 'totalPages') {
        totalPagesValue = parseInt(child.textContent) || 1;
        break;
      }
    }
    setTotalPages(totalPagesValue);

  } catch (err) {
    console.error('Error in fetchCities:', err);
    if (err.response?.status === 401) {
      setError('Неверные учетные данные. Пожалуйста, проверьте логин и пароль.');
      setShowAuthForm(true);
    } else {
      setError(`Ошибка загрузки городов: ${err.message}`);
    }
  } finally {
    setLoading(false);
  }
};
// Вспомогательная функция для парсинга одного города
const parseCityElement = (cityElement) => {
  const city = {};

  // Парсим все дочерние элементы
  for (let i = 0; i < cityElement.children.length; i++) {
    const child = cityElement.children[i];
    const tagName = child.tagName;
    const textContent = child.textContent;

    switch (tagName) {
      case 'id':
        city.id = parseInt(textContent) || 0;
        break;
      case 'name':
        city.name = textContent || '';
        break;
      case 'area':
        city.area = parseInt(textContent) || 0;
        break;
      case 'population':
        city.population = parseInt(textContent) || 0;
        break;
      case 'carCode':
        city.carCode = parseInt(textContent) || 0;
        break;
      case 'climate':
        city.climate = textContent || '';
        break;
      case 'standardOfLiving':
        city.standardOfLiving = textContent || '';
        break;
      case 'creationDate':
        city.creationDate = textContent || '';
        break;
      case 'metersAboveSeaLevel':
        city.metersAboveSeaLevel = parseFloat(textContent) || null;
        break;
      case 'coordinates':
        city.coordinates = parseCoordinatesElement(child);
        break;
      case 'governor':
        city.governor = parseGovernorElement(child);
        break;
      default:
        // Игнорируем неизвестные элементы
        break;
    }
  }

  return city;
};

// Парсинг координат
const parseCoordinatesElement = (coordinatesElement) => {
  const coordinates = { x: 0, y: 0 };

  for (let i = 0; i < coordinatesElement.children.length; i++) {
    const child = coordinatesElement.children[i];
    if (child.tagName === 'x') {
      coordinates.x = parseFloat(child.textContent) || 0;
    } else if (child.tagName === 'y') {
      coordinates.y = parseFloat(child.textContent) || 0;
    }
  }

  return coordinates;
};

// Парсинг губернатора
const parseGovernorElement = (governorElement) => {
  const governor = { name: '' };

  for (let i = 0; i < governorElement.children.length; i++) {
    const child = governorElement.children[i];
    if (child.tagName === 'name') {
      governor.name = child.textContent || '';
    } else if (child.tagName === 'age') {
      governor.age = parseInt(child.textContent) || null;
    } else if (child.tagName === 'height') {
      governor.height = parseFloat(child.textContent) || null;
    }
  }

  return governor;
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
        setError('Неверные учетные данные для сервиса популяции.');
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
        setError('Неверные учетные данные для сервиса популяции.');
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
            {showGenocideForm ? 'Скрыть популяцию' : 'Популяция'}
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
            <h2>Операции с популяцией</h2>

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

        <div className="table-container">
          {loading ? (
            <div>Загрузка...</div>
          ) : (
            <>
              <div className="table-controls">
                <div className="page-size-control">
                  <label>Городов на странице: </label>
                  <select
                    value={pageSize}
                    onChange={(e) => {
                      setPageSize(Number(e.target.value));
                      setPage(1); // Сброс на первую страницу при изменении размера
                    }}
                  >
                    <option value={5}>5</option>
                    <option value={10}>10</option>
                    <option value={20}>20</option>
                    <option value={50}>50</option>
                  </select>
                </div>
              </div>

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
                      <td>
                        {city.name && city.name.length > 50 ? (
                          <span title={city.name}>
                            {city.name.substring(0, 47)}...
                          </span>
                        ) : (
                          city.name
                        )}
                      </td>
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
