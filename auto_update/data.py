import requests
import zipfile
from constants import (
    SUNSET_DATA_URL,
    WEATHER_3DAY_DATA_URL,
    WEATHER_7DAY_DATA_URL,
    XML_NAMESPACE,
    ZIP_CODE,
    SUNSET_DATA_NEEDED,
)
from pathlib import Path
from xml.etree.ElementTree import ElementTree


def find_node(root, name):
    while name.startswith('/'):
        length = name.find('/', 1)
        if length == -1:
            name = name[1:]
            break
        else:
            root = root.find('all:' + name[1:length], XML_NAMESPACE)
            name = name[length:]
    return root.find('all:' + name, XML_NAMESPACE)


def url_retrieve(url: str, outfile: Path):
    r = requests.get(url)
    if r.status_code != 200:
        raise ConnectionError(f"Failed to download {url}\nWith error code: {r.status_code}")
    outfile.write_bytes(r.content)


class SunsetData(object):
    def __init__(self, download_path: Path = Path('sunset.xml')):
        self.download_path = download_path
        self.all_sunset_data = []
        url_retrieve(SUNSET_DATA_URL, download_path)

    def format_sunset_data(self):
        tree = ElementTree(file=self.download_path)
        root = tree.getroot()
        locations = find_node(root, '/dataset/locations')
        for location in locations.iterfind('all:location', XML_NAMESPACE):
            location_name = find_node(location, 'locationName').text
            area_data = []
            for time in location.iterfind('all:time', XML_NAMESPACE):
                data_time = find_node(time, 'dataTime').text
                single_set = [location_name, data_time]
                for parameter in time.iterfind('all:parameter', XML_NAMESPACE):
                    parameter_name = parameter.find('all:parameterName', XML_NAMESPACE).text
                    if parameter_name in SUNSET_DATA_NEEDED:
                        single_set.append(parameter.find('all:parameterValue', XML_NAMESPACE).text)
                area_data.append(tuple(single_set))
            self.all_sunset_data += area_data


class WeatherData3day(object):
    """
        資料欄位 = ['zip_code', 'time', 'T', 'RH', 'PoP6h', 'CI', 'Wx', 'WeatherDescription']
    """

    def __init__(self, download_path: Path = Path('data.zip')):
        url_retrieve(WEATHER_3DAY_DATA_URL, download_path)
        self.all_weather_data = []
        with zipfile.ZipFile(download_path) as ZF:
            self.zip_file_list = ZF.namelist()
            self.zip_file_list = [f for f in self.zip_file_list if not f.find("72hr_CH") == -1]
            self.zip_file_list.remove("TAIWAN_72hr_CH.xml")
            for file_name in self.zip_file_list:
                ZF.extract(file_name)

    def format_weather_data(self):
        weather_data_needed = ['T', 'RH', 'CI', 'Wx', 'WeatherDescription']
        for file_name in self.zip_file_list:
            tree = ElementTree(file=file_name)
            root = tree.getroot()
            locations = find_node(root, "/dataset/locations")
            for location in locations.iterfind("all:location", XML_NAMESPACE):
                location_name = find_node(location, "locationName")
                weather_ele = location.find("all:weatherElement", XML_NAMESPACE)
                weather_data = []
                for all_time in weather_ele.iterfind("all:time", XML_NAMESPACE):
                    time_str = all_time.find("all:dataTime", XML_NAMESPACE).text
                    t_loc = time_str.find('T')
                    plus_loc = time_str.find('+')
                    weather_data.append([time_str[:t_loc] + " " + time_str[t_loc + 1:plus_loc]])
                for weather_ele in location.iterfind("all:weatherElement", XML_NAMESPACE):
                    node = weather_ele.find("all:elementName", XML_NAMESPACE)
                    if node.text == 'PoP6h':
                        node_iter = weather_ele.iterfind("all:time", XML_NAMESPACE)
                        for i in range(12):
                            try:
                                node = next(node_iter)
                                weather_data[2 * i].append(int(find_node(node, "/elementValue/value").text))
                                weather_data[2 * i + 1].append(None)
                            except StopIteration:
                                weather_data[2 * i].append(None)
                                weather_data[2 * i + 1].append(None)

                    if node.text in weather_data_needed:
                        node_iter = weather_ele.iterfind("all:time", XML_NAMESPACE)
                        for i in range(24):
                            node = next(node_iter)
                            text = str(find_node(node, "/elementValue/value").text)
                            if text.isnumeric():
                                weather_data[i].append(int(text))
                            else:
                                weather_data[i].append(text)
                for i in range(24):
                    single_set = [ZIP_CODE[find_node(locations, "locationsName").text][location_name.text]]
                    single_set += weather_data[i]
                    self.all_weather_data.append(single_set)


class WeatherData7day(object):
    """
        資料欄位 = ['zip_code', 'time', 'T', 'RH', 'PoP6h', 'CI', 'Wx', 'WeatherDescription']
    """

    def __init__(self, download_path: Path = Path('data.zip')):
        url_retrieve(WEATHER_7DAY_DATA_URL, download_path)
        self.all_weather_data = []
        with zipfile.ZipFile(download_path) as ZF:
            self.zip_file_list = ZF.namelist()
            self.zip_file_list = [f for f in self.zip_file_list if not f.find("Week24_CH") == -1]
            self.zip_file_list.remove("TAIWAN_Week24_CH.xml")
            for file_name in self.zip_file_list:
                ZF.extract(file_name)

    def format_weather_data(self):
        weather_data_needed = ['T', 'RH', 'Wx', 'WeatherDescription', 'PoP24h']
        for file_name in self.zip_file_list:
            tree = ElementTree(file=file_name)
            root = tree.getroot()
            locations = find_node(root, "/dataset/locations")
            for location in locations.iterfind("all:location", XML_NAMESPACE):
                location_name = find_node(location, "locationName")
                weather_ele = location.find("all:weatherElement", XML_NAMESPACE)
                weather_data = []
                for all_time in weather_ele.iterfind("all:time", XML_NAMESPACE):
                    time_str = all_time.find("all:startTime", XML_NAMESPACE).text
                    t_loc = time_str.find('T')
                    plus_loc = time_str.find('+')
                    weather_data.append([time_str[:t_loc] + " " + time_str[t_loc + 1:plus_loc]])
                for weather_ele in location.iterfind("all:weatherElement", XML_NAMESPACE):
                    node = weather_ele.find("all:elementName", XML_NAMESPACE)
                    node_iter = weather_ele.iterfind("all:time", XML_NAMESPACE)
                    if node.text == "MaxCI":
                        CI = [0 for i in range(7)]
                        for i in range(7):
                            try:
                                node = next(node_iter)
                                text = int(find_node(node, "/elementValue/value").text)
                                CI[i] += text
                            except StopIteration:
                                pass
                    elif node.text == "MinCI":
                        for i in range(7):
                            try:
                                node = next(node_iter)
                                text = int(find_node(node, "/elementValue/value").text)
                                weather_data[i].append(int((CI[i] + text) / 2))
                            except StopIteration:
                                pass
                    if node.text in weather_data_needed:
                        for i in range(7):
                            node = next(node_iter)
                            text = str(find_node(node, "/elementValue/value").text)
                            if text.isnumeric():
                                weather_data[i].append(int(text))
                            elif text.isspace():
                                weather_data[i].append(None)
                            else:
                                weather_data[i].append(text)
                for i in range(7):
                    single_set = [ZIP_CODE[find_node(locations, "locationsName").text][location_name.text]]
                    # single_set.append(Re.find_node(locations,"locationsName").text)
                    # single_set.append(location_name.text)
                    single_set += weather_data[i]
                    self.all_weather_data.append(single_set)


if __name__ == '__main__':
    data = SunsetData()
    data.format_sunset_data()
