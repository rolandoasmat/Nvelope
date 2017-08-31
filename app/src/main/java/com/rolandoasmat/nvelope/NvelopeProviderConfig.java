package com.rolandoasmat.nvelope;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Created by rolandoasmat on 8/31/17.
 */
@SimpleSQLConfig(
        name = "NvelopeProvider",
        authority = "com.rolando.asmat.nvelope.provider",
        database = "nvelope.db",
        version = 1)
public class NvelopeProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
