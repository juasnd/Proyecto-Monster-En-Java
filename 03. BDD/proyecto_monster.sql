-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 12-06-2026 a las 14:34:35
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `proyecto monster`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `gepro_proyec`
--

CREATE TABLE `gepro_proyec` (
  `GEPRO_CODIGO` char(3) NOT NULL,
  `PEDEP_CODIGO` char(3) NOT NULL,
  `GEPRO_NOMBRE` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad que se utiliza para almacenar los PROYECTOS\r\n';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ge_peemp_gepro`
--

CREATE TABLE `ge_peemp_gepro` (
  `GEPRO_CODIGO` char(3) NOT NULL,
  `PEEMP_CODIGO` char(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Relacin entre PEEMP_EMPLEA y GEPRO_PROYEC';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pecar_cargo`
--

CREATE TABLE `pecar_cargo` (
  `PEDEP_CODIGO` char(3) NOT NULL,
  `PECAR_CODIGO` char(3) NOT NULL,
  `PECAR_DESCRI` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para representar el CARGO dentro de un DEP';

--
-- Volcado de datos para la tabla `pecar_cargo`
--

INSERT INTO `pecar_cargo` (`PEDEP_CODIGO`, `PECAR_CODIGO`, `PECAR_DESCRI`) VALUES
('ADA', 'CON', 'Contador Financiero'),
('DMA', 'GER', 'Gerente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedep_depart`
--

CREATE TABLE `pedep_depart` (
  `PEDEP_CODIGO` char(3) NOT NULL,
  `PEDEP_DESCRI` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad que se utiliza para almacenar los departamentos';

--
-- Volcado de datos para la tabla `pedep_depart`
--

INSERT INTO `pedep_depart` (`PEDEP_CODIGO`, `PEDEP_DESCRI`) VALUES
('ADA', 'Administacion'),
('DMA', 'recursos humanos');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `peemp_emplea`
--

CREATE TABLE `peemp_emplea` (
  `PEEMP_CODIGO` char(10) NOT NULL,
  `PEDEP_CODIGO` char(3) NOT NULL,
  `PECAR_CODIGO` char(3) NOT NULL,
  `PEPER_CODIGO` char(10) NOT NULL,
  `PED_PEDEP_CODIGO` char(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad que se utiliza para almacenar los empleados';

--
-- Volcado de datos para la tabla `peemp_emplea`
--

INSERT INTO `peemp_emplea` (`PEEMP_CODIGO`, `PEDEP_CODIGO`, `PECAR_CODIGO`, `PEPER_CODIGO`, `PED_PEDEP_CODIGO`) VALUES
('ADA', 'ADA', 'CON', 'ASD', 'ADA'),
('EMP2', 'DMA', 'GER', 'PER2', 'DMA'),
('EMP3', 'ADA', 'CON', 'PER3', 'ADA'),
('EMP4', 'ADA', 'CON', 'PER4', 'ADA');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `peesc_estciv`
--

CREATE TABLE `peesc_estciv` (
  `PEESC_CODIGO` char(1) NOT NULL,
  `PEESC_DESCRI` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad que se utiliza para almacenar el estado civil';

--
-- Volcado de datos para la tabla `peesc_estciv`
--

INSERT INTO `peesc_estciv` (`PEESC_CODIGO`, `PEESC_DESCRI`) VALUES
('C', 'Casado/a'),
('D', 'Divorciado/a'),
('P', 'Separado/a'),
('S', 'Soltero/a'),
('U', 'Union libre'),
('V', 'Viudo/a');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pefam_famil`
--

CREATE TABLE `pefam_famil` (
  `PEFAM_CODIGO` char(10) NOT NULL,
  `PEPER_CODIGO` char(10) NOT NULL,
  `PEPAR_CODIGO` char(3) NOT NULL,
  `PEFAM_NOMBRE` varchar(30) NOT NULL,
  `PEFAM_APELLIDO` varchar(30) NOT NULL,
  `PEFAM_FECHANACI` date NOT NULL,
  `PEFAM_TELEFONO` char(10) DEFAULT NULL,
  `PEFAM_CARGA` char(1) NOT NULL DEFAULT 'N',
  `PEFAM_OBSER` varchar(200) DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `pefam_famil`
--

INSERT INTO `pefam_famil` (`PEFAM_CODIGO`, `PEPER_CODIGO`, `PEPAR_CODIGO`, `PEFAM_NOMBRE`, `PEFAM_APELLIDO`, `PEFAM_FECHANACI`, `PEFAM_TELEFONO`, `PEFAM_CARGA`, `PEFAM_OBSER`) VALUES
('FAM0000001', 'ASD', 'MAD', 'Nelly', 'Vargas', '2000-11-09', '0995595716', 'S', 'Es mi mama, el ser supremo'),
('FAM0000003', 'PER3', 'ABU', 'Gloria', 'Toca', '1978-11-11', '0995595720', 'S', 'La abuelita de Heliana con problemas de piel.');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pefor_formac`
--

CREATE TABLE `pefor_formac` (
  `PEFOR_CODIGO` char(10) NOT NULL,
  `PEEMP_CODIGO` char(10) NOT NULL,
  `PEFOR_NIVEL` varchar(40) NOT NULL,
  `PEFOR_TITULO` varchar(80) NOT NULL,
  `PEFOR_INSTITUCION` varchar(80) NOT NULL,
  `PEFOR_FECINI` date NOT NULL,
  `PEFOR_FECFIN` date DEFAULT NULL,
  `PEFOR_OBSERVA` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pefor_formac`
--

INSERT INTO `pefor_formac` (`PEFOR_CODIGO`, `PEEMP_CODIGO`, `PEFOR_NIVEL`, `PEFOR_TITULO`, `PEFOR_INSTITUCION`, `PEFOR_FECINI`, `PEFOR_FECFIN`, `PEFOR_OBSERVA`) VALUES
('FOR0000001', 'ADA', 'Tercer Nivel', 'Ingeniero', 'Espe', '2003-02-10', '2010-02-10', 'Ingeniero de sowar'),
('FOR0000003', 'EMP3', 'Tercer Nivel', 'Agobada Financiera', 'Catolica', '2019-09-11', '2025-02-11', 'Abogada Financiera la mejor del Ecuador.'),
('FOR0000004', 'EMP4', 'Tercer Nivel', 'Ingeniero', 'Espe', '2019-09-11', '2025-02-11', 'Ineniero de sowar y con maestria'),
('FOR0000005', 'EMP2', 'Tercer Nivel', 'Ingeniero', 'Espe', '2019-09-10', '2025-02-10', 'Ingeniero de Auditorias Contables');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pepar_parent`
--

CREATE TABLE `pepar_parent` (
  `PEPAR_CODIGO` char(3) NOT NULL,
  `PEPAR_DESCRI` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para representar el PARENTESCO de un FAMIL';

--
-- Volcado de datos para la tabla `pepar_parent`
--

INSERT INTO `pepar_parent` (`PEPAR_CODIGO`, `PEPAR_DESCRI`) VALUES
('ABU', 'Abuelo/a'),
('ESP', 'Esposo/a'),
('HER', 'Hermano/a'),
('HIJ', 'Hijo/a'),
('MAD', 'Madre'),
('OTR', 'Otro'),
('PAD', 'Padre'),
('PRI', 'Primo/a'),
('SOB', 'Sobrino/a'),
('TIO', 'Tio/a');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `peper_person`
--

CREATE TABLE `peper_person` (
  `PEPER_CODIGO` char(10) NOT NULL,
  `PESEX_CODIGO` char(1) NOT NULL,
  `PEESC_CODIGO` char(1) DEFAULT NULL,
  `PEPER_NOMBRE` varchar(15) NOT NULL,
  `PEPER_APELLIDO` varchar(15) NOT NULL,
  `PEPER_CEDULA` char(10) DEFAULT NULL,
  `PEPER_FECHANACI` date NOT NULL,
  `PEPER_CARGAS` decimal(2,0) NOT NULL,
  `PEPER_DIRECCION` varchar(100) NOT NULL,
  `PEPER_CELULAR` char(10) DEFAULT NULL,
  `PEPER_TELDOM` char(10) DEFAULT NULL,
  `PEPER_EMAIL` varchar(100) NOT NULL,
  `PEPER_FOTO` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Contiene la informacion Personal de las personas';

--
-- Volcado de datos para la tabla `peper_person`
--

INSERT INTO `peper_person` (`PEPER_CODIGO`, `PESEX_CODIGO`, `PEESC_CODIGO`, `PEPER_NOMBRE`, `PEPER_APELLIDO`, `PEPER_CEDULA`, `PEPER_FECHANACI`, `PEPER_CARGAS`, `PEPER_DIRECCION`, `PEPER_CELULAR`, `PEPER_TELDOM`, `PEPER_EMAIL`, `PEPER_FOTO`) VALUES
('ASD', 'F', 'C', 'Juan Felipe', 'Gutierrez', '1726871526', '2004-02-05', 1, 'san bartolo', '0995595716', '0295595716', 'loljuanfeli@gmail.com', 'uploads/empleados/ASD_1780957816397.png'),
('PER0000001', 'M', NULL, 'Monster', 'Sistema', '0000000000', '2000-01-01', 0, 'Sin direccion', '0999999999', '0222222222', 'monster@edu.ec', NULL),
('PER2', 'F', 'S', 'Stalyn Joseph', 'Leiton', '0502080161', '2001-11-10', 0, 'La Ecuatoriana', '0995595716', '0295595716', 'st@gmail.com', 'uploads/empleados/PER2_1781194778411.png'),
('PER3', 'F', 'S', 'Heliana', 'Vargas', '1726871526', '1998-02-11', 0, 'Luis Dresel y Emilio Mullendorf Oe2-74', '0992845195', '0292845195', 'helina@gmail.com', 'uploads/empleados/PER3_1781195226840.png'),
('PER4', 'F', 'S', 'NIcolas', 'Zurica', '1726871526', '1998-02-11', 0, 'Luis Dresel y Emilio Mullendorf Oe2-74', '0992845195', '0292845195', 'helina@gmail.com', 'uploads/empleados/PER4_1781197106412.png');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pesex_sexo`
--

CREATE TABLE `pesex_sexo` (
  `PESEX_CODIGO` char(1) NOT NULL,
  `PESEX_DESCRI` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para representar el sexo o genero de una e';

--
-- Volcado de datos para la tabla `pesex_sexo`
--

INSERT INTO `pesex_sexo` (`PESEX_CODIGO`, `PESEX_DESCRI`) VALUES
('F', 'Femenino'),
('M', 'Masculino');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeest_estado`
--

CREATE TABLE `xeest_estado` (
  `XEEST_CODIGO` char(1) NOT NULL,
  `XEEST_DESCRI` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para gestionar el estado de las difetrente';

--
-- Volcado de datos para la tabla `xeest_estado`
--

INSERT INTO `xeest_estado` (`XEEST_CODIGO`, `XEEST_DESCRI`) VALUES
('A', 'ACTIVO'),
('B', 'BLOQUEADO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeopc_opcion`
--

CREATE TABLE `xeopc_opcion` (
  `XEOPC_CODIGO` char(3) NOT NULL,
  `XESIS_CODIGO` char(1) NOT NULL,
  `XEOPC_DESCRI` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para realizar el registro de las diferente';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeoxp_opcper`
--

CREATE TABLE `xeoxp_opcper` (
  `XEOPC_CODIGO` char(3) NOT NULL,
  `XEPER_CODIGO` char(8) NOT NULL,
  `XEOXP_FECASI` date NOT NULL,
  `XEOXP_FECRET` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para llevar el registro de las opciones qu';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeper_perfil`
--

CREATE TABLE `xeper_perfil` (
  `XEPER_CODIGO` char(8) NOT NULL,
  `XEPER_DESCRI` varchar(100) NOT NULL,
  `XEPER_OBSER` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para realizar la gestin de los diferentes';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xesis_sistem`
--

CREATE TABLE `xesis_sistem` (
  `XESIS_CODIGO` char(1) NOT NULL,
  `XESIS_DESCRI` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para realziar la gestin de los diferentes';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeusu_usuari`
--

CREATE TABLE `xeusu_usuari` (
  `PEPER_CODIGO` char(10) NOT NULL,
  `XEUSU_PASWD` varchar(255) NOT NULL,
  `XEUSU_FECCRE` datetime NOT NULL,
  `XEUSU_FECMOD` datetime NOT NULL,
  `XEUSU_PIEFIR` varchar(100) NOT NULL,
  `XEUSU_LOGIN` varchar(50) NOT NULL,
  `XEEST_CODIGO` char(1) NOT NULL,
  `XEUSU_TOKEN_REC` varchar(100) DEFAULT NULL,
  `XEUSU_FEC_EXP_TOK` datetime DEFAULT NULL,
  `XEUSU_INTENTOS` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad relacionada para gentionar los usuario que ingresan ';

--
-- Volcado de datos para la tabla `xeusu_usuari`
--

INSERT INTO `xeusu_usuari` (`PEPER_CODIGO`, `XEUSU_PASWD`, `XEUSU_FECCRE`, `XEUSU_FECMOD`, `XEUSU_PIEFIR`, `XEUSU_LOGIN`, `XEEST_CODIGO`, `XEUSU_TOKEN_REC`, `XEUSU_FEC_EXP_TOK`, `XEUSU_INTENTOS`) VALUES
('PER0000001', '81dc9bdb52d04dc20036dbd8313ed055', '2026-05-20 07:51:04', '2026-05-20 07:51:04', 'GUTIERREZ - LANDAZURI - LEITON', 'monster', 'A', NULL, NULL, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeuxp_usuper`
--

CREATE TABLE `xeuxp_usuper` (
  `PEPER_CODIGO` char(10) NOT NULL,
  `XEUSU_LOGIN` varchar(50) NOT NULL,
  `XEPER_CODIGO` char(8) NOT NULL,
  `XEUXP_FECASI` date NOT NULL,
  `XEUXP_FECRET` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para realizar el registro de los diferente';

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `gepro_proyec`
--
ALTER TABLE `gepro_proyec`
  ADD PRIMARY KEY (`GEPRO_CODIGO`),
  ADD KEY `FK_GR_GEPRO_PEDEP` (`PEDEP_CODIGO`);

--
-- Indices de la tabla `ge_peemp_gepro`
--
ALTER TABLE `ge_peemp_gepro`
  ADD PRIMARY KEY (`GEPRO_CODIGO`,`PEEMP_CODIGO`),
  ADD KEY `FK_GE_PEEMP_GEPRO2` (`PEEMP_CODIGO`);

--
-- Indices de la tabla `pecar_cargo`
--
ALTER TABLE `pecar_cargo`
  ADD PRIMARY KEY (`PEDEP_CODIGO`,`PECAR_CODIGO`);

--
-- Indices de la tabla `pedep_depart`
--
ALTER TABLE `pedep_depart`
  ADD PRIMARY KEY (`PEDEP_CODIGO`);

--
-- Indices de la tabla `peemp_emplea`
--
ALTER TABLE `peemp_emplea`
  ADD PRIMARY KEY (`PEEMP_CODIGO`),
  ADD KEY `FK_GR_PEDEP_PEEMP` (`PED_PEDEP_CODIGO`),
  ADD KEY `FK_PR_PEPER_PEEMP` (`PEPER_CODIGO`),
  ADD KEY `FK_PR_PEROL_PEEMP` (`PEDEP_CODIGO`,`PECAR_CODIGO`);

--
-- Indices de la tabla `peesc_estciv`
--
ALTER TABLE `peesc_estciv`
  ADD PRIMARY KEY (`PEESC_CODIGO`);

--
-- Indices de la tabla `pefam_famil`
--
ALTER TABLE `pefam_famil`
  ADD PRIMARY KEY (`PEFAM_CODIGO`),
  ADD KEY `FK_PR_PEPER_PEFAM` (`PEPER_CODIGO`),
  ADD KEY `FK_PR_PEPAR_PEFAM` (`PEPAR_CODIGO`);

--
-- Indices de la tabla `pefor_formac`
--
ALTER TABLE `pefor_formac`
  ADD PRIMARY KEY (`PEFOR_CODIGO`),
  ADD KEY `FK_PEFOR_PEEMP` (`PEEMP_CODIGO`);

--
-- Indices de la tabla `pepar_parent`
--
ALTER TABLE `pepar_parent`
  ADD PRIMARY KEY (`PEPAR_CODIGO`);

--
-- Indices de la tabla `peper_person`
--
ALTER TABLE `peper_person`
  ADD PRIMARY KEY (`PEPER_CODIGO`),
  ADD KEY `FK_PR_PEESC_PEPER` (`PEESC_CODIGO`),
  ADD KEY `FK_PR_PESEX_PEPER` (`PESEX_CODIGO`);

--
-- Indices de la tabla `pesex_sexo`
--
ALTER TABLE `pesex_sexo`
  ADD PRIMARY KEY (`PESEX_CODIGO`);

--
-- Indices de la tabla `xeest_estado`
--
ALTER TABLE `xeest_estado`
  ADD PRIMARY KEY (`XEEST_CODIGO`);

--
-- Indices de la tabla `xeopc_opcion`
--
ALTER TABLE `xeopc_opcion`
  ADD PRIMARY KEY (`XEOPC_CODIGO`),
  ADD KEY `FK_XR_XESIS_XEOPC` (`XESIS_CODIGO`);

--
-- Indices de la tabla `xeoxp_opcper`
--
ALTER TABLE `xeoxp_opcper`
  ADD PRIMARY KEY (`XEOPC_CODIGO`,`XEPER_CODIGO`,`XEOXP_FECASI`),
  ADD KEY `FK_XR_XEPER_XEOXP` (`XEPER_CODIGO`);

--
-- Indices de la tabla `xeper_perfil`
--
ALTER TABLE `xeper_perfil`
  ADD PRIMARY KEY (`XEPER_CODIGO`);

--
-- Indices de la tabla `xesis_sistem`
--
ALTER TABLE `xesis_sistem`
  ADD PRIMARY KEY (`XESIS_CODIGO`);

--
-- Indices de la tabla `xeusu_usuari`
--
ALTER TABLE `xeusu_usuari`
  ADD PRIMARY KEY (`PEPER_CODIGO`,`XEUSU_LOGIN`),
  ADD KEY `FK_XR_XEEST_XEUSU` (`XEEST_CODIGO`);

--
-- Indices de la tabla `xeuxp_usuper`
--
ALTER TABLE `xeuxp_usuper`
  ADD PRIMARY KEY (`PEPER_CODIGO`,`XEUSU_LOGIN`,`XEPER_CODIGO`),
  ADD KEY `FK_XR_XEPER_XEUXP` (`XEPER_CODIGO`);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `gepro_proyec`
--
ALTER TABLE `gepro_proyec`
  ADD CONSTRAINT `FK_GR_GEPRO_PEDEP` FOREIGN KEY (`PEDEP_CODIGO`) REFERENCES `pedep_depart` (`PEDEP_CODIGO`);

--
-- Filtros para la tabla `ge_peemp_gepro`
--
ALTER TABLE `ge_peemp_gepro`
  ADD CONSTRAINT `FK_GE_PEEMP_GEPRO` FOREIGN KEY (`GEPRO_CODIGO`) REFERENCES `gepro_proyec` (`GEPRO_CODIGO`),
  ADD CONSTRAINT `FK_GE_PEEMP_GEPRO2` FOREIGN KEY (`PEEMP_CODIGO`) REFERENCES `peemp_emplea` (`PEEMP_CODIGO`);

--
-- Filtros para la tabla `pecar_cargo`
--
ALTER TABLE `pecar_cargo`
  ADD CONSTRAINT `FK_PR_PEDEP_PECAR` FOREIGN KEY (`PEDEP_CODIGO`) REFERENCES `pedep_depart` (`PEDEP_CODIGO`);

--
-- Filtros para la tabla `peemp_emplea`
--
ALTER TABLE `peemp_emplea`
  ADD CONSTRAINT `FK_GR_PEDEP_PEEMP` FOREIGN KEY (`PED_PEDEP_CODIGO`) REFERENCES `pedep_depart` (`PEDEP_CODIGO`),
  ADD CONSTRAINT `FK_PR_PEPER_PEEMP` FOREIGN KEY (`PEPER_CODIGO`) REFERENCES `peper_person` (`PEPER_CODIGO`),
  ADD CONSTRAINT `FK_PR_PEROL_PEEMP` FOREIGN KEY (`PEDEP_CODIGO`,`PECAR_CODIGO`) REFERENCES `pecar_cargo` (`PEDEP_CODIGO`, `PECAR_CODIGO`);

--
-- Filtros para la tabla `pefam_famil`
--
ALTER TABLE `pefam_famil`
  ADD CONSTRAINT `FK_PR_PEPAR_PEFAM` FOREIGN KEY (`PEPAR_CODIGO`) REFERENCES `pepar_parent` (`PEPAR_CODIGO`),
  ADD CONSTRAINT `FK_PR_PEPER_PEFAM` FOREIGN KEY (`PEPER_CODIGO`) REFERENCES `peper_person` (`PEPER_CODIGO`);

--
-- Filtros para la tabla `pefor_formac`
--
ALTER TABLE `pefor_formac`
  ADD CONSTRAINT `FK_PEFOR_PEEMP` FOREIGN KEY (`PEEMP_CODIGO`) REFERENCES `peemp_emplea` (`PEEMP_CODIGO`);

--
-- Filtros para la tabla `peper_person`
--
ALTER TABLE `peper_person`
  ADD CONSTRAINT `FK_PR_PEESC_PEPER` FOREIGN KEY (`PEESC_CODIGO`) REFERENCES `peesc_estciv` (`PEESC_CODIGO`),
  ADD CONSTRAINT `FK_PR_PESEX_PEPER` FOREIGN KEY (`PESEX_CODIGO`) REFERENCES `pesex_sexo` (`PESEX_CODIGO`);

--
-- Filtros para la tabla `xeopc_opcion`
--
ALTER TABLE `xeopc_opcion`
  ADD CONSTRAINT `FK_XR_XESIS_XEOPC` FOREIGN KEY (`XESIS_CODIGO`) REFERENCES `xesis_sistem` (`XESIS_CODIGO`);

--
-- Filtros para la tabla `xeoxp_opcper`
--
ALTER TABLE `xeoxp_opcper`
  ADD CONSTRAINT `FK_XR_XEOPC_XEOXP` FOREIGN KEY (`XEOPC_CODIGO`) REFERENCES `xeopc_opcion` (`XEOPC_CODIGO`),
  ADD CONSTRAINT `FK_XR_XEPER_XEOXP` FOREIGN KEY (`XEPER_CODIGO`) REFERENCES `xeper_perfil` (`XEPER_CODIGO`);

--
-- Filtros para la tabla `xeusu_usuari`
--
ALTER TABLE `xeusu_usuari`
  ADD CONSTRAINT `FK_XR_PEEMP_XEUSU` FOREIGN KEY (`PEPER_CODIGO`) REFERENCES `peper_person` (`PEPER_CODIGO`),
  ADD CONSTRAINT `FK_XR_XEEST_XEUSU` FOREIGN KEY (`XEEST_CODIGO`) REFERENCES `xeest_estado` (`XEEST_CODIGO`);

--
-- Filtros para la tabla `xeuxp_usuper`
--
ALTER TABLE `xeuxp_usuper`
  ADD CONSTRAINT `FK_XR_XEPER_XEUXP` FOREIGN KEY (`XEPER_CODIGO`) REFERENCES `xeper_perfil` (`XEPER_CODIGO`),
  ADD CONSTRAINT `FK_XR_XEUSU_XEUXP` FOREIGN KEY (`PEPER_CODIGO`,`XEUSU_LOGIN`) REFERENCES `xeusu_usuari` (`PEPER_CODIGO`, `XEUSU_LOGIN`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
