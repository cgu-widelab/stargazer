<?PHP
	class mypdo extends PDO
	{
		private $_dsn = "mysql:host=db;dbname=stargazer";
		private $_user = 'stargazer';
		private $_password = '12345';
		private $_encode = 'utf8';
		private $statement;
		private $_data = [];

		function __construct()
		{
			try
			{
				parent::__construct($this->_dsn,$this->_user,$this->_password);
				$this->_setEncode();
			}catch(PDOException $e)
			{
				print_r($e);
			}
		}
		
		function __set($name, $value)
		{
			$this->data[$name] = $value;
		}
		
		function __get($name)
		{
			if(isset($this->_data[$name]))
			{
				return $this->_data[$name];
			}
		}
		
		private function _setEncode()
		{
			$this->query("SET NAMES '{$this->_encode}'");
		}
		
		private function _bind($bind)
		{
			foreach($bind as $key =>$value)
			{
				$this->statement->bindValue($key,$value,is_numeric($value)?PDO::PARAM_INT:PDO::PARAM_STR);
			}
		}
		
		function bindQuery($sql,array $bind = [])
		{
			$this->statement = $this->prepare($sql);
			$this->_bind($bind);
			$this->statement->execute();
			return $this->statement->fetchAll(PDO::FETCH_ASSOC);
		}
		
		function error()
		{
			$error = $this->statement->errorInfo();
			echo 'errorCode'.$error[0].'<br>';
			echo 'errorString'.$error[2];
		}
		
		function insert($table, array $param = [])
		{
			$columns = array_keys($param);
			$values = [];
			$bind_data = [];
			foreach($param as $key => $value)
			{
				$values [] = ":{$key}";
				$bind_data [":{$key}"] = $value;
			}
			$sql = "INSERT INTO {$table} (" .implode(',',$columns) .") VALUES (".implode(',',$values) .")";
			$this->statement = $this->prepare($sql);
			$this->_bind($bind_data);
			$this->statement->execute();
		}
	}
	
	
?>