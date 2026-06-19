-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 19-06-2026 a las 03:13:36
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
('DEV', 'DVS', 'Analista de pruebas'),
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
('DEV', 'Desarrollo'),
('DMA', 'Recursos Humanos');

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
('EMP0000013', 'DEV', 'DVS', 'PER0000011', 'DEV'),
('EMP2', 'DMA', 'GER', 'PER2', 'DMA'),
('EMP6', 'ADA', 'CON', 'PER6', 'ADA');

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
('FAM0000002', 'PER6', 'HIJ', 'Nicolas', 'Leiton', '2005-02-11', '0992845195', 'S', 'Es el hijo de Leiton'),
('FAM0000003', 'ASD', 'MAD', 'Nelly', 'Vargas', '2000-11-08', '0995595716', 'S', 'Es mi mama, el ser supremo');

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
('FOR0000006', 'EMP6', 'Tercer Nivel', 'Ingeniero', 'ESPE', '2019-09-11', '2025-02-11', 'Ineniero de software y con maestria'),
('FOR0000007', 'EMP2', 'Tercer Nivel', 'Ingeniero', 'Espe', '2019-09-09', '2025-02-09', 'Ingeniero de Auditorias Contables'),
('FOR0000008', 'ADA', 'Tercer Nivel', 'Ingeniero', 'Espe', '2003-02-09', '2010-02-09', 'Ingeniero de sowar'),
('FOR0000009', 'EMP0000013', 'Tercer Nivel', 'Ingeniero', 'Saleciana', '2019-09-11', '2025-02-11', 'Ingeniero De Software');

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
  `PEPER_TIPO` char(3) NOT NULL DEFAULT 'INV',
  `PESEX_CODIGO` char(1) DEFAULT NULL,
  `PEESC_CODIGO` char(1) DEFAULT NULL,
  `PEPER_NOMBRE` varchar(15) NOT NULL,
  `PEPER_APELLIDO` varchar(15) NOT NULL,
  `PEPER_CEDULA` char(10) DEFAULT NULL,
  `PEPER_FECHANACI` date DEFAULT NULL,
  `PEPER_CARGAS` decimal(2,0) NOT NULL DEFAULT 0,
  `PEPER_DIRECCION` varchar(100) DEFAULT NULL,
  `PEPER_CELULAR` char(10) DEFAULT NULL,
  `PEPER_TELDOM` char(10) DEFAULT NULL,
  `PEPER_EMAIL` varchar(100) DEFAULT NULL,
  `PEPER_FOTO` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Contiene la informacion Personal de las personas';

--
-- Volcado de datos para la tabla `peper_person`
--

INSERT INTO `peper_person` (`PEPER_CODIGO`, `PEPER_TIPO`, `PESEX_CODIGO`, `PEESC_CODIGO`, `PEPER_NOMBRE`, `PEPER_APELLIDO`, `PEPER_CEDULA`, `PEPER_FECHANACI`, `PEPER_CARGAS`, `PEPER_DIRECCION`, `PEPER_CELULAR`, `PEPER_TELDOM`, `PEPER_EMAIL`, `PEPER_FOTO`) VALUES
('ASD', 'EMP', 'F', 'C', 'Juan', 'Gutierrez', '1726871526', '2004-02-04', 1, 'san bartolo', '0995595716', '0295595716', 'loljuanfeli@gmail.com', 'uploads/empleados/ASD_1780957816397.png'),
('PER0000001', 'ADM', 'M', NULL, 'Monster', 'Sistema', '0000000000', '2000-01-01', 0, 'Sin direccion', '0999999999', '0222222222', 'monster@edu.ec', NULL),
('PER0000011', 'EMP', 'F', 'S', 'Delia', 'Vargas', '1751483627', '2000-11-11', 0, 'San Bartolo', '0962789771', '0262789771', 'Delia@gmail.com', 'uploads/empleados/1751483627_1781662407428.png'),
('PER0000012', 'INV', NULL, NULL, 'Taty', 'Noriega', '172469', NULL, 0, 'Sin direccion', NULL, NULL, '', NULL),
('PER2', 'EMP', 'F', 'S', 'Stalyn Joseph', 'Leiton', '0502080161', '2001-11-09', 0, 'La Ecuatoriana', '0995595716', '0295595716', 'st@gmail.com', 'uploads/empleados/PER2_1781618566411.png'),
('PER6', 'EMP', 'F', 'S', 'Jose', 'Leiton', '1726871526', '1998-02-11', 1, 'Luis Dresel y Emilio Mullendorf Oe2-74', '0992845195', '0292845195', 'helina@gmail.com', 'uploads/empleados/PER6_1781527016595.png');

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
-- Estructura de tabla para la tabla `petip_persona`
--

CREATE TABLE `petip_persona` (
  `PETIP_CODIGO` char(3) NOT NULL,
  `PETIP_DESCRI` varchar(50) NOT NULL,
  `PETIP_OBSER` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `petip_persona`
--

INSERT INTO `petip_persona` (`PETIP_CODIGO`, `PETIP_DESCRI`, `PETIP_OBSER`) VALUES
('ADM', 'Administrador', 'Persona con perfil administrativo.'),
('CLI', 'Cliente', 'Usuario cliente del sistema.'),
('EMP', 'Empleado', 'Persona registrada como empleado interno.'),
('INV', 'Invitado', 'Usuario externo o temporal sin relacion laboral.'),
('RHH', 'Recursos Humanos', 'Persona relacionada con gestion de personal.');

-- --------------------------------------------------------

--
-- Estructura Stand-in para la vista `vw_empleados_sin_usuario`
-- (Véase abajo para la vista actual)
--
CREATE TABLE `vw_empleados_sin_usuario` (
`PEEMP_CODIGO` char(10)
,`PEPER_CODIGO` char(10)
,`PEPER_TIPO` char(3)
,`PEPER_NOMBRE` varchar(15)
,`PEPER_APELLIDO` varchar(15)
,`PEPER_CEDULA` char(10)
,`PEPER_EMAIL` varchar(100)
,`PEDEP_DESCRI` varchar(50)
,`PECAR_DESCRI` varchar(50)
);

-- --------------------------------------------------------

--
-- Estructura Stand-in para la vista `vw_menu_usuario`
-- (Véase abajo para la vista actual)
--
CREATE TABLE `vw_menu_usuario` (
`PEPER_CODIGO` char(10)
,`XEUSU_LOGIN` varchar(50)
,`XEPER_CODIGO` mediumtext
,`XEPER_DESCRI` mediumtext
,`XEOPC_CODIGO` char(3)
,`XEOPC_DESCRI` varchar(100)
,`XEOPC_URL` varchar(150)
,`XEOPC_ICONO` varchar(50)
,`XEOPC_ORDEN` int(11)
,`XEOXP_VER` varchar(1)
,`XEOXP_CREAR` varchar(1)
,`XEOXP_EDITAR` varchar(1)
,`XEOXP_ELIMINAR` varchar(1)
);

-- --------------------------------------------------------

--
-- Estructura Stand-in para la vista `vw_permisos_usuario`
-- (Véase abajo para la vista actual)
--
CREATE TABLE `vw_permisos_usuario` (
`PEPER_CODIGO` char(10)
,`XEUSU_LOGIN` varchar(50)
,`XEOPC_CODIGO` char(3)
,`XEOPC_DESCRI` varchar(100)
,`XEOPC_URL` varchar(150)
,`PUEDE_VER` varchar(1)
,`PUEDE_CREAR` varchar(1)
,`PUEDE_EDITAR` varchar(1)
,`PUEDE_ELIMINAR` varchar(1)
);

-- --------------------------------------------------------

--
-- Estructura Stand-in para la vista `vw_personas_sin_usuario`
-- (Véase abajo para la vista actual)
--
CREATE TABLE `vw_personas_sin_usuario` (
`PEPER_CODIGO` char(10)
,`PEPER_TIPO` char(3)
,`PEPER_TIPO_DESCRI` varchar(50)
,`PEPER_NOMBRE` varchar(15)
,`PEPER_APELLIDO` varchar(15)
,`PEPER_CEDULA` char(10)
,`PEPER_EMAIL` varchar(100)
,`ES_EMPLEADO` varchar(1)
,`PEEMP_CODIGO` char(10)
);

-- --------------------------------------------------------

--
-- Estructura Stand-in para la vista `vw_usuarios_perfiles`
-- (Véase abajo para la vista actual)
--
CREATE TABLE `vw_usuarios_perfiles` (
`PEPER_CODIGO` char(10)
,`XEUSU_LOGIN` varchar(50)
,`XEEST_CODIGO` char(1)
,`XEEST_DESCRI` varchar(50)
,`XEUSU_FECCRE` datetime
,`XEUSU_FECMOD` datetime
,`XEUSU_INTENTOS` int(11)
,`XEUSU_CAMBIO_CLAVE` char(1)
,`XEUSU_ULTIMO_ACCESO` datetime
,`XEUSU_BLOQUEADO_HASTA` datetime
,`PEPER_TIPO` char(3)
,`PEPER_TIPO_DESCRI` varchar(50)
,`PEPER_NOMBRE` varchar(15)
,`PEPER_APELLIDO` varchar(15)
,`PEPER_CEDULA` char(10)
,`PEPER_EMAIL` varchar(100)
,`ES_EMPLEADO` varchar(1)
,`PEEMP_CODIGO` char(10)
,`XEPER_CODIGO` char(8)
,`XEPER_DESCRI` varchar(100)
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeaud_auditoria`
--

CREATE TABLE `xeaud_auditoria` (
  `XEAUD_CODIGO` int(11) NOT NULL,
  `PEPER_CODIGO` char(10) DEFAULT NULL,
  `XEUSU_LOGIN` varchar(50) DEFAULT NULL,
  `XEAUD_ACCION` varchar(50) NOT NULL,
  `XEAUD_TABLA` varchar(50) NOT NULL,
  `XEAUD_DETALLE` varchar(255) DEFAULT NULL,
  `XEAUD_FECHA` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `xeaud_auditoria`
--

INSERT INTO `xeaud_auditoria` (`XEAUD_CODIGO`, `PEPER_CODIGO`, `XEUSU_LOGIN`, `XEAUD_ACCION`, `XEAUD_TABLA`, `XEAUD_DETALLE`, `XEAUD_FECHA`) VALUES
(1, NULL, 'sistema', 'CREAR', 'PEEMP_EMPLEA', 'Empleado EMP0000013 y usuario 1751483627 creados automaticamente.', '2026-06-17 02:13:27');

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
('B', 'BLOQUEADO'),
('I', 'INACTIVO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xegen_codigo`
--

CREATE TABLE `xegen_codigo` (
  `XEGEN_TABLA` varchar(50) NOT NULL,
  `XEGEN_CAMPO` varchar(50) NOT NULL,
  `XEGEN_PREFIJO` varchar(5) NOT NULL,
  `XEGEN_LONGITUD` int(11) NOT NULL,
  `XEGEN_ULTIMO` int(11) NOT NULL DEFAULT 0,
  `XEGEN_DESCRI` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `xegen_codigo`
--

INSERT INTO `xegen_codigo` (`XEGEN_TABLA`, `XEGEN_CAMPO`, `XEGEN_PREFIJO`, `XEGEN_LONGITUD`, `XEGEN_ULTIMO`, `XEGEN_DESCRI`) VALUES
('gepro_proyec', 'GEPRO_CODIGO', 'PRO', 3, 0, 'Codigo autogenerable de proyecto'),
('peemp_emplea', 'PEEMP_CODIGO', 'EMP', 10, 13, 'Codigo autogenerable de empleado'),
('pefam_famil', 'PEFAM_CODIGO', 'FAM', 10, 3, 'Codigo autogenerable de familiar'),
('pefor_formac', 'PEFOR_CODIGO', 'FOR', 10, 9, 'Codigo autogenerable de formacion'),
('peper_person', 'PEPER_CODIGO', 'PER', 10, 12, 'Codigo autogenerable de persona');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeopc_opcion`
--

CREATE TABLE `xeopc_opcion` (
  `XEOPC_CODIGO` char(3) NOT NULL,
  `XESIS_CODIGO` char(1) NOT NULL,
  `XEOPC_DESCRI` varchar(100) NOT NULL,
  `XEOPC_URL` varchar(150) DEFAULT NULL,
  `XEOPC_ICONO` varchar(50) DEFAULT NULL,
  `XEOPC_ORDEN` int(11) DEFAULT 0,
  `XEOPC_ESTADO` char(1) DEFAULT 'A'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para realizar el registro de las diferente';

--
-- Volcado de datos para la tabla `xeopc_opcion`
--

INSERT INTO `xeopc_opcion` (`XEOPC_CODIGO`, `XESIS_CODIGO`, `XEOPC_DESCRI`, `XEOPC_URL`, `XEOPC_ICONO`, `XEOPC_ORDEN`, `XEOPC_ESTADO`) VALUES
('CAR', 'M', 'Cargos', 'cargos.jsp', 'briefcase', 3, 'A'),
('DEP', 'M', 'Departamentos', 'departamentos.jsp', 'building', 2, 'A'),
('EMP', 'M', 'Empleados', 'empleados.jsp', 'users', 4, 'A'),
('FAM', 'M', 'Familiares', 'familiares.jsp', 'family', 5, 'A'),
('FOR', 'M', 'Formacion', 'formacion.jsp', 'graduation', 6, 'A'),
('INI', 'M', 'Inicio', 'pagPrincipal.jsp', 'home', 1, 'A'),
('OCP', 'M', 'Permisos', 'permisos.jsp', 'key', 9, 'A'),
('PER', 'M', 'Perfiles', 'perfiles.jsp', 'shield', 8, 'A'),
('RPT', 'M', 'Reportes', 'reportes.jsp', 'file', 10, 'A'),
('USU', 'M', 'Usuarios', 'usuarios.jsp', 'user-lock', 7, 'A');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeoxp_opcper`
--

CREATE TABLE `xeoxp_opcper` (
  `XEOPC_CODIGO` char(3) NOT NULL,
  `XEPER_CODIGO` char(8) NOT NULL,
  `XEOXP_FECASI` date NOT NULL,
  `XEOXP_FECRET` date DEFAULT NULL,
  `XEOXP_VER` char(1) NOT NULL DEFAULT 'S',
  `XEOXP_CREAR` char(1) NOT NULL DEFAULT 'N',
  `XEOXP_EDITAR` char(1) NOT NULL DEFAULT 'N',
  `XEOXP_ELIMINAR` char(1) NOT NULL DEFAULT 'N'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para llevar el registro de las opciones qu';

--
-- Volcado de datos para la tabla `xeoxp_opcper`
--

INSERT INTO `xeoxp_opcper` (`XEOPC_CODIGO`, `XEPER_CODIGO`, `XEOXP_FECASI`, `XEOXP_FECRET`, `XEOXP_VER`, `XEOXP_CREAR`, `XEOXP_EDITAR`, `XEOXP_ELIMINAR`) VALUES
('CAR', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('CAR', 'RRHH', '2026-06-17', NULL, 'S', 'S', 'S', 'N'),
('DEP', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('DEP', 'EMPLEADO', '2026-06-17', NULL, 'S', 'N', 'N', 'N'),
('DEP', 'RRHH', '2026-06-17', NULL, 'S', 'S', 'S', 'N'),
('EMP', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('EMP', 'RRHH', '2026-06-17', NULL, 'S', 'S', 'S', 'N'),
('FAM', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('FAM', 'RRHH', '2026-06-17', NULL, 'S', 'S', 'S', 'N'),
('FOR', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('FOR', 'RRHH', '2026-06-17', NULL, 'S', 'S', 'S', 'N'),
('INI', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('INI', 'CLIENTE', '2026-06-17', NULL, 'S', 'N', 'N', 'N'),
('INI', 'EMPLEADO', '2026-06-17', NULL, 'S', 'N', 'N', 'N'),
('INI', 'INVITADO', '2026-06-17', NULL, 'S', 'N', 'N', 'N'),
('INI', 'RRHH', '2026-06-17', NULL, 'S', 'N', 'N', 'N'),
('OCP', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('PER', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('PER', 'PRUEBAS', '2026-06-17', NULL, 'S', 'N', 'N', 'N'),
('RPT', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('RPT', 'RRHH', '2026-06-17', NULL, 'S', 'S', 'S', 'N'),
('USU', 'ADMIN', '2026-06-15', NULL, 'S', 'S', 'S', 'S'),
('USU', 'PRUEBAS', '2026-06-17', NULL, 'S', 'N', 'N', 'N');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeper_perfil`
--

CREATE TABLE `xeper_perfil` (
  `XEPER_CODIGO` char(8) NOT NULL,
  `XEPER_DESCRI` varchar(100) NOT NULL,
  `XEPER_OBSER` text DEFAULT NULL,
  `XEPER_ESTADO` char(1) DEFAULT 'A'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para realizar la gestin de los diferentes';

--
-- Volcado de datos para la tabla `xeper_perfil`
--

INSERT INTO `xeper_perfil` (`XEPER_CODIGO`, `XEPER_DESCRI`, `XEPER_OBSER`, `XEPER_ESTADO`) VALUES
('ADMIN', 'Administrador', 'Perfil con acceso total al sistema.', 'A'),
('CLIENTE', 'Cliente', 'Perfil para usuarios clientes del sistema.', 'A'),
('EMPLEADO', 'Empleado', 'Perfil basico para usuarios empleados.', 'A'),
('INVITADO', 'Invitado', 'Perfil externo con acceso limitado.', 'A'),
('PRUEBAS', 'Pruebas', NULL, 'A'),
('RRHH', 'Recursos Humanos', 'Perfil encargado de gestionar personal y reportes.', 'A');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeres_reset_clave`
--

CREATE TABLE `xeres_reset_clave` (
  `XERES_CODIGO` int(11) NOT NULL,
  `PEPER_CODIGO` char(10) NOT NULL,
  `XEUSU_LOGIN` varchar(50) NOT NULL,
  `XERES_TOKEN` varchar(150) NOT NULL,
  `XERES_FECHA_CREACION` datetime NOT NULL DEFAULT current_timestamp(),
  `XERES_FECHA_EXPIRA` datetime NOT NULL,
  `XERES_USADO` char(1) NOT NULL DEFAULT 'N'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xeses_sesion`
--

CREATE TABLE `xeses_sesion` (
  `XESES_CODIGO` int(11) NOT NULL,
  `PEPER_CODIGO` char(10) NOT NULL,
  `XEUSU_LOGIN` varchar(50) NOT NULL,
  `XESES_FECHA_INICIO` datetime NOT NULL DEFAULT current_timestamp(),
  `XESES_FECHA_FIN` datetime DEFAULT NULL,
  `XESES_IP` varchar(45) DEFAULT NULL,
  `XESES_NAVEGADOR` varchar(200) DEFAULT NULL,
  `XESES_ESTADO` char(1) NOT NULL DEFAULT 'A'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `xesis_sistem`
--

CREATE TABLE `xesis_sistem` (
  `XESIS_CODIGO` char(1) NOT NULL,
  `XESIS_DESCRI` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad utilizada para realziar la gestin de los diferentes';

--
-- Volcado de datos para la tabla `xesis_sistem`
--

INSERT INTO `xesis_sistem` (`XESIS_CODIGO`, `XESIS_DESCRI`) VALUES
('M', 'Sistema Monster');

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
  `XEUSU_INTENTOS` int(11) DEFAULT 0,
  `XEUSU_CAMBIO_CLAVE` char(1) NOT NULL DEFAULT 'S',
  `XEUSU_ULTIMO_ACCESO` datetime DEFAULT NULL,
  `XEUSU_BLOQUEADO_HASTA` datetime DEFAULT NULL,
  `XEUSU_ALGORITMO` varchar(30) NOT NULL DEFAULT 'MD5',
  `XEUSU_OBSERVACION` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Entidad relacionada para gentionar los usuario que ingresan ';

--
-- Volcado de datos para la tabla `xeusu_usuari`
--

INSERT INTO `xeusu_usuari` (`PEPER_CODIGO`, `XEUSU_PASWD`, `XEUSU_FECCRE`, `XEUSU_FECMOD`, `XEUSU_PIEFIR`, `XEUSU_LOGIN`, `XEEST_CODIGO`, `XEUSU_TOKEN_REC`, `XEUSU_FEC_EXP_TOK`, `XEUSU_INTENTOS`, `XEUSU_CAMBIO_CLAVE`, `XEUSU_ULTIMO_ACCESO`, `XEUSU_BLOQUEADO_HASTA`, `XEUSU_ALGORITMO`, `XEUSU_OBSERVACION`) VALUES
('ASD', 'e10adc3949ba59abbe56e057f20f883e', '2026-06-15 22:52:11', '2026-06-15 22:52:11', 'GUTIERREZ - LANDAZURI - LEITON', '1726871526', 'A', NULL, NULL, 0, 'N', '2026-06-18 00:48:27', NULL, 'MD5', NULL),
('PER0000001', '81dc9bdb52d04dc20036dbd8313ed055', '2026-05-20 07:51:04', '2026-05-20 07:51:04', 'GUTIERREZ - LANDAZURI - LEITON', 'monster', 'A', NULL, NULL, 0, 'N', '2026-06-19 01:10:11', NULL, 'MD5', NULL),
('PER0000011', '81dc9bdb52d04dc20036dbd8313ed055', '2026-06-17 02:13:27', '2026-06-17 02:13:27', 'GUTIERREZ - LANDAZURI - LEITON', '1751483627', 'A', NULL, NULL, 0, 'N', '2026-06-17 02:51:27', NULL, 'MD5', NULL),
('PER0000012', '5940f48049a8dba5a721057eb72b422b', '2026-06-18 00:49:26', '2026-06-18 00:49:26', 'GUTIERREZ - LANDAZURI - LEITON', 'Taty123', 'A', NULL, NULL, 0, 'N', '2026-06-18 00:50:42', NULL, 'MD5', NULL),
('PER2', '81dc9bdb52d04dc20036dbd8313ed055', '2026-06-17 03:01:04', '2026-06-17 03:01:04', 'GUTIERREZ - LANDAZURI - LEITON', '0502080161', 'B', NULL, NULL, 0, 'N', '2026-06-17 13:11:34', NULL, 'MD5', NULL);

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
-- Volcado de datos para la tabla `xeuxp_usuper`
--

INSERT INTO `xeuxp_usuper` (`PEPER_CODIGO`, `XEUSU_LOGIN`, `XEPER_CODIGO`, `XEUXP_FECASI`, `XEUXP_FECRET`) VALUES
('ASD', '1726871526', 'INVITADO', '2026-06-18', NULL),
('PER0000001', 'monster', 'ADMIN', '2026-06-15', NULL),
('PER0000011', '1751483627', 'ADMIN', '2026-06-17', NULL),
('PER0000012', 'Taty123', 'INVITADO', '2026-06-18', NULL),
('PER2', '0502080161', 'RRHH', '2026-06-17', NULL);

-- --------------------------------------------------------

--
-- Estructura para la vista `vw_empleados_sin_usuario`
--
DROP TABLE IF EXISTS `vw_empleados_sin_usuario`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_empleados_sin_usuario`  AS SELECT `emp`.`PEEMP_CODIGO` AS `PEEMP_CODIGO`, `per`.`PEPER_CODIGO` AS `PEPER_CODIGO`, `per`.`PEPER_TIPO` AS `PEPER_TIPO`, `per`.`PEPER_NOMBRE` AS `PEPER_NOMBRE`, `per`.`PEPER_APELLIDO` AS `PEPER_APELLIDO`, `per`.`PEPER_CEDULA` AS `PEPER_CEDULA`, `per`.`PEPER_EMAIL` AS `PEPER_EMAIL`, `dep`.`PEDEP_DESCRI` AS `PEDEP_DESCRI`, `car`.`PECAR_DESCRI` AS `PECAR_DESCRI` FROM ((((`peemp_emplea` `emp` join `peper_person` `per` on(`emp`.`PEPER_CODIGO` = `per`.`PEPER_CODIGO`)) join `pedep_depart` `dep` on(`emp`.`PEDEP_CODIGO` = `dep`.`PEDEP_CODIGO`)) join `pecar_cargo` `car` on(`emp`.`PEDEP_CODIGO` = `car`.`PEDEP_CODIGO` and `emp`.`PECAR_CODIGO` = `car`.`PECAR_CODIGO`)) left join `xeusu_usuari` `usu` on(`per`.`PEPER_CODIGO` = `usu`.`PEPER_CODIGO`)) WHERE `usu`.`PEPER_CODIGO` is null ;

-- --------------------------------------------------------

--
-- Estructura para la vista `vw_menu_usuario`
--
DROP TABLE IF EXISTS `vw_menu_usuario`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_menu_usuario`  AS SELECT `u`.`PEPER_CODIGO` AS `PEPER_CODIGO`, `u`.`XEUSU_LOGIN` AS `XEUSU_LOGIN`, group_concat(distinct `pf`.`XEPER_CODIGO` order by `pf`.`XEPER_CODIGO` ASC separator ',') AS `XEPER_CODIGO`, group_concat(distinct `pf`.`XEPER_DESCRI` order by `pf`.`XEPER_DESCRI` ASC separator ', ') AS `XEPER_DESCRI`, `op`.`XEOPC_CODIGO` AS `XEOPC_CODIGO`, `op`.`XEOPC_DESCRI` AS `XEOPC_DESCRI`, `op`.`XEOPC_URL` AS `XEOPC_URL`, `op`.`XEOPC_ICONO` AS `XEOPC_ICONO`, `op`.`XEOPC_ORDEN` AS `XEOPC_ORDEN`, CASE WHEN max(`oxp`.`XEOXP_VER` = 'S') = 1 THEN 'S' ELSE 'N' END AS `XEOXP_VER`, CASE WHEN max(`oxp`.`XEOXP_CREAR` = 'S') = 1 THEN 'S' ELSE 'N' END AS `XEOXP_CREAR`, CASE WHEN max(`oxp`.`XEOXP_EDITAR` = 'S') = 1 THEN 'S' ELSE 'N' END AS `XEOXP_EDITAR`, CASE WHEN max(`oxp`.`XEOXP_ELIMINAR` = 'S') = 1 THEN 'S' ELSE 'N' END AS `XEOXP_ELIMINAR` FROM ((((`xeusu_usuari` `u` join `xeuxp_usuper` `up` on(`u`.`PEPER_CODIGO` = `up`.`PEPER_CODIGO` and `u`.`XEUSU_LOGIN` = `up`.`XEUSU_LOGIN` and `up`.`XEUXP_FECRET` is null)) join `xeper_perfil` `pf` on(`up`.`XEPER_CODIGO` = `pf`.`XEPER_CODIGO` and `pf`.`XEPER_ESTADO` = 'A')) join `xeoxp_opcper` `oxp` on(`pf`.`XEPER_CODIGO` = `oxp`.`XEPER_CODIGO` and `oxp`.`XEOXP_FECRET` is null)) join `xeopc_opcion` `op` on(`oxp`.`XEOPC_CODIGO` = `op`.`XEOPC_CODIGO` and `op`.`XEOPC_ESTADO` = 'A')) WHERE `u`.`XEEST_CODIGO` = 'A' GROUP BY `u`.`PEPER_CODIGO`, `u`.`XEUSU_LOGIN`, `op`.`XEOPC_CODIGO`, `op`.`XEOPC_DESCRI`, `op`.`XEOPC_URL`, `op`.`XEOPC_ICONO`, `op`.`XEOPC_ORDEN` HAVING max(`oxp`.`XEOXP_VER` = 'S') = 1 ;

-- --------------------------------------------------------

--
-- Estructura para la vista `vw_permisos_usuario`
--
DROP TABLE IF EXISTS `vw_permisos_usuario`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_permisos_usuario`  AS SELECT `u`.`PEPER_CODIGO` AS `PEPER_CODIGO`, `u`.`XEUSU_LOGIN` AS `XEUSU_LOGIN`, `op`.`XEOPC_CODIGO` AS `XEOPC_CODIGO`, `op`.`XEOPC_DESCRI` AS `XEOPC_DESCRI`, `op`.`XEOPC_URL` AS `XEOPC_URL`, CASE WHEN max(`oxp`.`XEOXP_VER` = 'S') = 1 THEN 'S' ELSE 'N' END AS `PUEDE_VER`, CASE WHEN max(`oxp`.`XEOXP_CREAR` = 'S') = 1 THEN 'S' ELSE 'N' END AS `PUEDE_CREAR`, CASE WHEN max(`oxp`.`XEOXP_EDITAR` = 'S') = 1 THEN 'S' ELSE 'N' END AS `PUEDE_EDITAR`, CASE WHEN max(`oxp`.`XEOXP_ELIMINAR` = 'S') = 1 THEN 'S' ELSE 'N' END AS `PUEDE_ELIMINAR` FROM ((((`xeusu_usuari` `u` join `xeuxp_usuper` `up` on(`u`.`PEPER_CODIGO` = `up`.`PEPER_CODIGO` and `u`.`XEUSU_LOGIN` = `up`.`XEUSU_LOGIN` and `up`.`XEUXP_FECRET` is null)) join `xeper_perfil` `pf` on(`up`.`XEPER_CODIGO` = `pf`.`XEPER_CODIGO` and `pf`.`XEPER_ESTADO` = 'A')) join `xeoxp_opcper` `oxp` on(`pf`.`XEPER_CODIGO` = `oxp`.`XEPER_CODIGO` and `oxp`.`XEOXP_FECRET` is null)) join `xeopc_opcion` `op` on(`oxp`.`XEOPC_CODIGO` = `op`.`XEOPC_CODIGO` and `op`.`XEOPC_ESTADO` = 'A')) WHERE `u`.`XEEST_CODIGO` = 'A' GROUP BY `u`.`PEPER_CODIGO`, `u`.`XEUSU_LOGIN`, `op`.`XEOPC_CODIGO`, `op`.`XEOPC_DESCRI`, `op`.`XEOPC_URL` ;

-- --------------------------------------------------------

--
-- Estructura para la vista `vw_personas_sin_usuario`
--
DROP TABLE IF EXISTS `vw_personas_sin_usuario`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_personas_sin_usuario`  AS SELECT `p`.`PEPER_CODIGO` AS `PEPER_CODIGO`, `p`.`PEPER_TIPO` AS `PEPER_TIPO`, `tp`.`PETIP_DESCRI` AS `PEPER_TIPO_DESCRI`, `p`.`PEPER_NOMBRE` AS `PEPER_NOMBRE`, `p`.`PEPER_APELLIDO` AS `PEPER_APELLIDO`, `p`.`PEPER_CEDULA` AS `PEPER_CEDULA`, `p`.`PEPER_EMAIL` AS `PEPER_EMAIL`, CASE WHEN `emp`.`PEEMP_CODIGO` is null THEN 'N' ELSE 'S' END AS `ES_EMPLEADO`, `emp`.`PEEMP_CODIGO` AS `PEEMP_CODIGO` FROM (((`peper_person` `p` left join `petip_persona` `tp` on(`p`.`PEPER_TIPO` = `tp`.`PETIP_CODIGO`)) left join `peemp_emplea` `emp` on(`p`.`PEPER_CODIGO` = `emp`.`PEPER_CODIGO`)) left join `xeusu_usuari` `u` on(`p`.`PEPER_CODIGO` = `u`.`PEPER_CODIGO`)) WHERE `u`.`PEPER_CODIGO` is null ;

-- --------------------------------------------------------

--
-- Estructura para la vista `vw_usuarios_perfiles`
--
DROP TABLE IF EXISTS `vw_usuarios_perfiles`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_usuarios_perfiles`  AS SELECT `u`.`PEPER_CODIGO` AS `PEPER_CODIGO`, `u`.`XEUSU_LOGIN` AS `XEUSU_LOGIN`, `u`.`XEEST_CODIGO` AS `XEEST_CODIGO`, `e`.`XEEST_DESCRI` AS `XEEST_DESCRI`, `u`.`XEUSU_FECCRE` AS `XEUSU_FECCRE`, `u`.`XEUSU_FECMOD` AS `XEUSU_FECMOD`, `u`.`XEUSU_INTENTOS` AS `XEUSU_INTENTOS`, `u`.`XEUSU_CAMBIO_CLAVE` AS `XEUSU_CAMBIO_CLAVE`, `u`.`XEUSU_ULTIMO_ACCESO` AS `XEUSU_ULTIMO_ACCESO`, `u`.`XEUSU_BLOQUEADO_HASTA` AS `XEUSU_BLOQUEADO_HASTA`, `p`.`PEPER_TIPO` AS `PEPER_TIPO`, `tp`.`PETIP_DESCRI` AS `PEPER_TIPO_DESCRI`, `p`.`PEPER_NOMBRE` AS `PEPER_NOMBRE`, `p`.`PEPER_APELLIDO` AS `PEPER_APELLIDO`, `p`.`PEPER_CEDULA` AS `PEPER_CEDULA`, `p`.`PEPER_EMAIL` AS `PEPER_EMAIL`, CASE WHEN `emp`.`PEEMP_CODIGO` is null THEN 'N' ELSE 'S' END AS `ES_EMPLEADO`, `emp`.`PEEMP_CODIGO` AS `PEEMP_CODIGO`, `up`.`XEPER_CODIGO` AS `XEPER_CODIGO`, `pf`.`XEPER_DESCRI` AS `XEPER_DESCRI` FROM ((((((`xeusu_usuari` `u` join `peper_person` `p` on(`u`.`PEPER_CODIGO` = `p`.`PEPER_CODIGO`)) left join `petip_persona` `tp` on(`p`.`PEPER_TIPO` = `tp`.`PETIP_CODIGO`)) join `xeest_estado` `e` on(`u`.`XEEST_CODIGO` = `e`.`XEEST_CODIGO`)) left join `peemp_emplea` `emp` on(`p`.`PEPER_CODIGO` = `emp`.`PEPER_CODIGO`)) left join `xeuxp_usuper` `up` on(`u`.`PEPER_CODIGO` = `up`.`PEPER_CODIGO` and `u`.`XEUSU_LOGIN` = `up`.`XEUSU_LOGIN` and `up`.`XEUXP_FECRET` is null)) left join `xeper_perfil` `pf` on(`up`.`XEPER_CODIGO` = `pf`.`XEPER_CODIGO`)) ;

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
  ADD KEY `FK_PR_PESEX_PEPER` (`PESEX_CODIGO`),
  ADD KEY `IDX_PEPER_CEDULA` (`PEPER_CEDULA`),
  ADD KEY `IDX_PEPER_TIPO` (`PEPER_TIPO`);

--
-- Indices de la tabla `pesex_sexo`
--
ALTER TABLE `pesex_sexo`
  ADD PRIMARY KEY (`PESEX_CODIGO`);

--
-- Indices de la tabla `petip_persona`
--
ALTER TABLE `petip_persona`
  ADD PRIMARY KEY (`PETIP_CODIGO`);

--
-- Indices de la tabla `xeaud_auditoria`
--
ALTER TABLE `xeaud_auditoria`
  ADD PRIMARY KEY (`XEAUD_CODIGO`),
  ADD KEY `IDX_XEAUD_USUARIO` (`PEPER_CODIGO`,`XEUSU_LOGIN`),
  ADD KEY `IDX_XEAUD_FECHA` (`XEAUD_FECHA`);

--
-- Indices de la tabla `xeest_estado`
--
ALTER TABLE `xeest_estado`
  ADD PRIMARY KEY (`XEEST_CODIGO`);

--
-- Indices de la tabla `xegen_codigo`
--
ALTER TABLE `xegen_codigo`
  ADD PRIMARY KEY (`XEGEN_TABLA`,`XEGEN_CAMPO`);

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
  ADD KEY `IDX_XEOXP_PERMISOS` (`XEPER_CODIGO`,`XEOPC_CODIGO`,`XEOXP_FECRET`);

--
-- Indices de la tabla `xeper_perfil`
--
ALTER TABLE `xeper_perfil`
  ADD PRIMARY KEY (`XEPER_CODIGO`);

--
-- Indices de la tabla `xeres_reset_clave`
--
ALTER TABLE `xeres_reset_clave`
  ADD PRIMARY KEY (`XERES_CODIGO`),
  ADD UNIQUE KEY `UK_XERES_TOKEN` (`XERES_TOKEN`),
  ADD KEY `IDX_XERES_USUARIO` (`PEPER_CODIGO`,`XEUSU_LOGIN`);

--
-- Indices de la tabla `xeses_sesion`
--
ALTER TABLE `xeses_sesion`
  ADD PRIMARY KEY (`XESES_CODIGO`),
  ADD KEY `IDX_XESES_USUARIO` (`PEPER_CODIGO`,`XEUSU_LOGIN`);

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
  ADD UNIQUE KEY `UK_XEUSU_LOGIN` (`XEUSU_LOGIN`),
  ADD KEY `FK_XR_XEEST_XEUSU` (`XEEST_CODIGO`);

--
-- Indices de la tabla `xeuxp_usuper`
--
ALTER TABLE `xeuxp_usuper`
  ADD PRIMARY KEY (`PEPER_CODIGO`,`XEUSU_LOGIN`,`XEPER_CODIGO`),
  ADD KEY `IDX_XEUXP_PERFIL_ACTIVO` (`XEPER_CODIGO`,`XEUXP_FECRET`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `xeaud_auditoria`
--
ALTER TABLE `xeaud_auditoria`
  MODIFY `XEAUD_CODIGO` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `xeres_reset_clave`
--
ALTER TABLE `xeres_reset_clave`
  MODIFY `XERES_CODIGO` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `xeses_sesion`
--
ALTER TABLE `xeses_sesion`
  MODIFY `XESES_CODIGO` int(11) NOT NULL AUTO_INCREMENT;

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
-- Filtros para la tabla `xeres_reset_clave`
--
ALTER TABLE `xeres_reset_clave`
  ADD CONSTRAINT `FK_XERES_XEUSU` FOREIGN KEY (`PEPER_CODIGO`,`XEUSU_LOGIN`) REFERENCES `xeusu_usuari` (`PEPER_CODIGO`, `XEUSU_LOGIN`);

--
-- Filtros para la tabla `xeses_sesion`
--
ALTER TABLE `xeses_sesion`
  ADD CONSTRAINT `FK_XESES_XEUSU` FOREIGN KEY (`PEPER_CODIGO`,`XEUSU_LOGIN`) REFERENCES `xeusu_usuari` (`PEPER_CODIGO`, `XEUSU_LOGIN`);

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
