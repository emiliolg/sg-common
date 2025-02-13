
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.Set;

import org.jetbrains.annotations.NonNls;

import tekgenesis.common.collections.Colls;

/**
 * Common global constants.
 */
@SuppressWarnings("DuplicateStringLiteralInspection")
public interface Constants {

    //~ Instance Fields ..............................................................................................................................

    String ACCEPT  = "accept";
    String ACCEPTS = "accepts";

    @NonNls String ANOTHER_MODELS       = "samples/another/src/main/mm/";
    String         APPLICATION_RESET_DB = "application.resetDB";
    String         APPLICATION_RUN_DIR  = "application.runDir";
    String         ARGUMENT             = "argument";
    @NonNls String AUTHORIZATION_MODELS = "projects/authorization/src/main/mm/";
    @NonNls String BASIC_MODELS         = "samples/basic/src/main/mm/";
    String         BEARER               = "Bearer ";
    @NonNls String BIGINT               = "bigint";
    String         BIN                  = "bin";

    String         BIN_DIR       = "bin.dir";
    @NonNls String BOOLEAN       = "boolean";
    String         BOOT          = "boot";
    String         BRANCH        = "build.branch";
    String         BUILD_APP     = "build.application";
    String         BUILD_NUMBER  = "build.number";
    String         BUILD_VERSION = "build.version";
    String         CANCEL        = "cancel";

    String CANNOT_CREATE_CLASS  = "Cannot create class ";
    String CANNOT_FIND_FIELD    = "Cannot find field ";
    String CANNOT_INVOKE_METHOD = "Cannot invoke method ";
    String CANT_BE_APPLIED      = "' can't be applied to ";

    String         CLASS          = "class";
    @NonNls String CLASSES_DIR    = "classes";
    String         COMPONENT_PATH = "META-INF/%s-component.properties";
    String         COMPONENTS_KEY = "application-components";

    String COMPONENTS_PATH = "META-INF/application-components.properties";
    String CONSTANT        = "constant";

    String CONSTRUCTOR_FOR = "Constructor for ";

    @NonNls String CSS_EXT         = "css";
    @NonNls String CURRENT_TEST_ID = "testId";

    // Keywords. todo ... move to other Constants file....

    String         DEFAULT         = "default";
    @NonNls String DEFAULT_PACKAGE = "domain";

    String         DEFAULT_SCOPE         = "";
    int            DEFAULT_STRING_LENGTH = 255;
    @NonNls String DEFINED               = "defined";

    String         DEPENDANTS       = ".dependants";
    @NonNls String DEPRECATED       = "deprecated";
    @NonNls String DEPRECATED_FALSE = " +_deprecated:false";

    String         DESCRIBED_BY = "described_by";
    @NonNls String DESCRIPTION  = "description";
    @NonNls String DOUBLE       = "double";

    /** An empty Array of integers. */
    int[] EMPTY_INT_ARRAY = new int[0];
    /** An empty Array of Objects. */
    Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    String   EMPTY_STRING       = "\"\"";
    /** An empty Array of Strings. */
    String[] EMPTY_STRING_ARRAY = new String[0];

    String         ENTITY           = "Entity";
    @NonNls String ENTITY_LIST_FILE = "META-INF/entity-list";

    String ENTITY_STRING = "entity";
    String ENUM          = "Enum '";

    String         ENUM_KEY_FIELD      = "NAME";
    String         ENUM_LABEL_FIELD    = "LABEL";
    String         ENUM_STRING         = "enum";
    String         EXCEPTION           = "Exception";
    @NonNls String FILE_NAME_URL_PARAM = "&filename=";
    @NonNls String FOCUS               = "focus";
    @NonNls String FOREIGN_KEY_SUFFIX  = "fk";
    String         FORM_STRING         = "form";
    String         FROM_MILLISECONDS   = "fromMilliseconds";
    String         FUNCTION            = "Function '";
    @NonNls String GENERATED           = "@Generated";

    int    HASH_SALT = 31;
    String HELP_URI  = "/sg/help/";

    int HEXADECIMAL_RADIX = 16;

    @NonNls String HIDE_TYPE  = "hideType";
    @NonNls String HTML_CLASS = "tekgenesis.service.html.Html";

    String HTTP_LOCALHOST = "http://localhost:";

    /** Sequential id field name (Used in Entities with default primary key). */
    @NonNls String ID        = "id";
    String         IMAGE_EXT = "image";

    /** The an index arg var name. */
    @NonNls String INDEX = "index";

    // DB suffixes
    @NonNls String INDEX_SUFFIX = "idxt";
    String         INPUT_PREFIX = "in";

    String         INSERT                      = "insert";
    String         INT                         = "int";
    @NonNls String INTERNAL_JDK                = "internalJdk";
    @NonNls String ISO_8859_1                  = "ISO-8859-1";
    @NonNls String DEFAULT_PROPERTIES_ENCODING = ISO_8859_1;
    String         EXPORT_METHOD               = "export";

    String FILTER = "FILTER";

    @NonNls String HTML_DIR         = "html/";
    @NonNls String HTML_EXT         = "html";
    @NonNls String HTTPS_PORT       = "suigeneris.httpsPort";
    String         INPUT            = "INPUT";
    @NonNls String INVALID          = "invalid";
    @NonNls String INVOKE           = "invoke";
    String         ITEM_IMAGE_CLASS = "item-image";

    @NonNls String JADE_EXT                    = "jade";
    @NonNls String JADE_INSTANCE_BUILDER_CLASS = "tekgenesis.service.html.HtmlInstanceBuilder.Jade";
    @NonNls String JAVA_CLASS_EXT              = ".class";

    @NonNls String JAVA_EXT       = ".java";
    @NonNls String JAVA_IO_TMPDIR = "java.io.tmpdir";

    String         JMX_INSTANCE_MAP = "JMX_NODES_INSTANCE";
    @NonNls String JS_EXT           = "js";

    int            KILO          = 1024;
    @NonNls String LESS_EXT      = "less";
    String         LIB           = "lib";
    @NonNls String LIFECYCLE_KEY = "lifecycle.key";
    @NonNls String LOCALHOST     = "localhost";

    String LOGIN_URI = "/login";

    int MAX_BYTE         = 0xff;
    int MAX_DB_ID_LENGTH = 30;

    String         MAX_STRING        = new String(new char[] { Character.MAX_VALUE });
    @NonNls String MD_EXT            = ".md";
    int            MEGA              = KILO * KILO;
    @NonNls String MEMBER            = "member";
    @NonNls String META_INF_SERVICES = "META-INF/services/";
    @NonNls String META_MODEL_EXT    = "mm";
    String         MOCK_PROTOCOL     = "mock:";

    @NonNls String MULTIPLE                        = "multiple";
    @NonNls String MUSTACHE_EXT                    = "mustache";
    @NonNls String MUSTACHE_INSTANCE_BUILDER_CLASS = "tekgenesis.service.html.HtmlInstanceBuilder.Mustache";
    String         NEWRELIC_AGENT_TRANSACTION_NAME = "com.newrelic.agent.TRANSACTION_NAME";
    @NonNls String NO_VALUE                        = "No value";

    String NOT_FOUND = "' not found";

    /** Representation of the null value as an string. */
    String         NULL_TO_STRING = "null";
    @NonNls String OBJECT         = "object";
    int            OCTAL_RADIX    = 8;

    String OPERATOR = "Operator '";

    String         OPTIONS        = "options";
    String         PACKAGE_SPC    = "package ";
    @NonNls String PERMISSION_ALL = "*";

    @NonNls String PLACEHOLDER            = "placeholder";
    String         PLUGGABLE_LOGGER_LEVEL = "logger.level";
    String         PLUGGABLE_LOGGERS      = "logging.loggers";
    @NonNls String PLUGIN_MM              = "plugin-mm";
    String         PORT_OPT               = "port";
    String         PRIMARY_KEY            = "primary_key";
    String         PRIMARY_KEY_PREFIX     = "PK_";
    String         PROCESS                = "process";
    String         PROCESSING             = "processing";
    @NonNls String PROPERTIES_EXT         = ".properties";
    @NonNls String REDIRECTION            = "redirection";

    @NonNls String REFRESH_KEY_REFERENCES = "refreshKeyReferences";
    String         REFRESH_URI            = "/sg/admin/refresh";

    @NonNls String REQ_UUID              = "req.uuid";
    @NonNls String REQUEST_METHOD        = "req.method";
    @NonNls String RESET                 = "reset";
    @NonNls String RESOURCE_SERVLET_PATH = "/sg/resource";
    @NonNls
    @SuppressWarnings("DuplicateStringLiteralInspection")
    String         RESOURCES = "resources";
    String         ROW       = ".row";
    String         SCHEDULE  = "schedule";

    @NonNls String SCHEMA_LIST_FILE = "META-INF/schema-list";
    String         SCRIPT           = "script";
    String         SDK_BUILD        = "build.number";
    String         SDK_VERSION      = "build.version";
    String         SEARCH_BY        = "search_by";

    /** Sequential id field name (Used in Inner Entities). */
    @NonNls String SEQ_ID                = "seqId";
    @NonNls String SEQUENCE_COLUMN_VALUE = "sequence-value";
    @NonNls String SEQUENCE_ID           = "sequence-id";
    @NonNls int    SEQUENCE_SIZE         = 10;

    // Sequence constants
    @NonNls  // this should be removed
    String                                   SEQUENCE_TABLE_NAME = "sequencer";
    @NonNls String                           SG_BUILD            = "sgBuild";
    String                                   SG_SERVICE_CACHE    = "sgServiceCache";

    String SG_SERVICENAME = "sg_jmxrmi";

    String SHIRO_ADMIN_PASS = "password";

    String SHIRO_ADMIN_ROLE = "admin";

    String SHIRO_ADMIN_USER = "admin";
    String SHIRO_GUEST_USER = "guest";

    /** Shiro active session.* */
    @SuppressWarnings("DuplicateStringLiteralInspection")
    String         SHIRO_SESSION_CACHE  = "shiro-activeSessionCache";
    @NonNls String SHOW_DEPRECABLE_INFO = "showDeprecableInfo";
    @NonNls String SHOWCASE_MODELS      = "samples/showcase/src/main/mm/";
    String         SOURCES              = "sources";
    String         SPACE                = " ";
    @NonNls String SQL_CURRENT_USER     = "$CURRENT_USER";
    @NonNls String SQL_DROP_DB          = "DROP_DB";

    @NonNls String STRING            = "String";
    @NonNls String SUI_GENERIS       = "Sui Generis";
    @NonNls String SUI_GENERIS_SDK   = "Sui Generis SDK";
    String         SUI_SCOPES_CACHE  = "suiScopesCache";
    String         SUI_SERVICE_CACHE = "suiServiceCache";

    String SUIGEN_DEVMODE = "suigen.devmode";

    String SUIGEN_PROPS = "suigen.properties";

    String         SUIGEN_SKIP_AUTH              = "suigen.skipauth";
    String         SUIGEN_SOURCES                = "suigen.sources";
    @NonNls String SUIGENERIS_BRANCH             = "suigeneris.branch";
    @NonNls String SUIGENERIS_BUILD              = "suigeneris.build";
    @NonNls String SUIGENERIS_FORM_INSTANCES_MAP = "suigeneris.form.map";
    @NonNls String SUIGENERIS_HANDLERS_CACHE     = "handlers-cache";
    @NonNls String SUIGENERIS_LIFE_CYCLE_MAP     = "suigeneris.lifecycle.map";

    @NonNls String SUIGENERIS_VERSION     = "suigeneris.version";
    @NonNls String SUIGENERIS_XHTML_CACHE = "xhtml-cache";

    @NonNls String SUMMARY = "summary";

    String SYMBOL = "Symbol '";

    String SYSTEM_RUNDIR = "user.dir";

    /** Static Fields of EntityInstance. */

    String         TABLE_FIELD_NAME  = "TABLE";
    @NonNls String TASK_LIST_FILE    = "META-INF/task-list";
    @NonNls String TEK_LOGO          = "/public/sg/img/logo-TekGenesis.png";
    String         TEKGENESIS        = "tekgenesis";
    String         TEKGENESIS_DOMAIN = "tekgenesis.com";
    String         TEST              = "test";

    String THE_CLASS = "The class '";

    String TO_BE_IMPLEMENTED = "To be implemented";

    @NonNls String TYPE               = "type";
    String         TYPE_NOT_SUPPORTED = "Type not supported: ";
    @NonNls String TYPES              = "types";

    String         UNIQUE        = "unique";
    @NonNls String UNIQUE_SUFFIX = "unqt";

    String UPDATE = "update";

    @NonNls String UTF8     = "UTF-8";
    @NonNls String UTF8_BOM = "\uFEFF";

    String         VALUE_OF           = "valueOf";
    @NonNls String VAR                = "var";
    String         VERSION_PROPERTIES = "version.properties";

    String WAIT_FOR_INDEX = "suigen.waitForIndex";
    String WEBAPP_DIR     = "webapp";

    // Not constants... but messages (require I18N) !
    String         WIDGET                       = "Widget '";
    String         WIDGET_UI                    = "WidgetUI '";
    @NonNls String XHTML_EXT                    = "xhtml";
    Set<String>    TEMPLATE_EXTS                = Colls.set(MUSTACHE_EXT, JADE_EXT, XHTML_EXT);
    @NonNls String XHTML_INSTANCE_BUILDER_CLASS = "tekgenesis.service.html.HtmlInstanceBuilder.Xhtml";
}  // end class Constants
