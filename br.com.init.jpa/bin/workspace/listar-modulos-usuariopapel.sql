SELECT distinct m.* 
 FROM dbacp.usuario_portal_papel up,
     dbacp.gstmob_papel_modulo pm,
     dbacp.gstmob_modulo m
 WHERE pm.cd_modulo_gestor = m.cd_modulo_gestor AND
     pm.cd_papel = up.cd_papel AND
     m.sn_ativo ='S' AND
     UPPER(up.cd_usuario_portal) = UPPER(:usuarioId)
 ORDER BY m.nr_ordem