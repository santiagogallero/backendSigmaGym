-- Notification Templates for Sigma Gym
-- Insert sample notification templates for all events and channels

-- BOOKING_CREATED templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('BOOKING_CREATED', 'EMAIL', 'Reserva Confirmada - ${serviceName}', 
'<h2>¡Hola!</h2><p>Tu reserva ha sido confirmada:</p><ul><li><strong>Servicio:</strong> ${serviceName}</li><li><strong>Fecha:</strong> ${appointmentDate}</li><li><strong>Entrenador:</strong> ${trainerName}</li></ul><p>¡Te esperamos en Sigma Gym!</p>', 
NULL, true, NOW()),

('BOOKING_CREATED', 'PUSH', 'Reserva Confirmada', 
'Tu reserva para ${serviceName} el ${appointmentDate} ha sido confirmada. ¡Te esperamos!', 
NULL, true, NOW()),

('BOOKING_CREATED', 'WHATSAPP', NULL, 
'¡Hola! Tu reserva para *${serviceName}* el ${appointmentDate} con ${trainerName} ha sido confirmada. ¡Te esperamos en Sigma Gym! 💪', 
'booking_confirmed', true, NOW());

-- BOOKING_CANCELLED templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('BOOKING_CANCELLED', 'EMAIL', 'Reserva Cancelada - ${serviceName}', 
'<h2>Reserva Cancelada</h2><p>Tu reserva ha sido cancelada:</p><ul><li><strong>Servicio:</strong> ${serviceName}</li><li><strong>Fecha:</strong> ${appointmentDate}</li><li><strong>Motivo:</strong> ${reason}</li></ul><p>Puedes hacer una nueva reserva cuando gustes.</p>', 
NULL, true, NOW()),

('BOOKING_CANCELLED', 'PUSH', 'Reserva Cancelada', 
'Tu reserva para ${serviceName} el ${appointmentDate} ha sido cancelada. Motivo: ${reason}', 
NULL, true, NOW()),

('BOOKING_CANCELLED', 'WHATSAPP', NULL, 
'Tu reserva para *${serviceName}* el ${appointmentDate} ha sido cancelada. Motivo: ${reason}. Puedes hacer una nueva reserva cuando gustes.', 
'booking_cancelled', true, NOW());

-- BOOKING_REMINDER templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('BOOKING_REMINDER', 'EMAIL', 'Recordatorio - Tu reserva es en ${timeUntilAppointment}', 
'<h2>¡No olvides tu cita!</h2><p>Tu reserva es en ${timeUntilAppointment}:</p><ul><li><strong>Servicio:</strong> ${serviceName}</li><li><strong>Fecha:</strong> ${appointmentDate}</li><li><strong>Entrenador:</strong> ${trainerName}</li></ul><p>¡Te esperamos en Sigma Gym!</p>', 
NULL, true, NOW()),

('BOOKING_REMINDER', 'PUSH', 'Recordatorio de Cita', 
'Tu reserva para ${serviceName} es en ${timeUntilAppointment}. ¡No faltes!', 
NULL, true, NOW()),

('BOOKING_REMINDER', 'WHATSAPP', NULL, 
'¡Recordatorio! Tu reserva para *${serviceName}* con ${trainerName} es en ${timeUntilAppointment}. ¡Te esperamos! ⏰', 
'booking_reminder', true, NOW());

-- PAYMENT_SUCCEEDED templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('PAYMENT_SUCCEEDED', 'EMAIL', 'Pago Exitoso - $${amount} ${currency}', 
'<h2>¡Pago Confirmado!</h2><p>Tu pago ha sido procesado exitosamente:</p><ul><li><strong>Monto:</strong> $${amount} ${currency}</li><li><strong>Descripción:</strong> ${description}</li><li><strong>Fecha:</strong> ${paymentDate}</li></ul><p>¡Gracias por confiar en Sigma Gym!</p>', 
NULL, true, NOW()),

('PAYMENT_SUCCEEDED', 'PUSH', 'Pago Exitoso', 
'Tu pago de $${amount} ${currency} ha sido procesado exitosamente. ¡Gracias!', 
NULL, true, NOW()),

('PAYMENT_SUCCEEDED', 'WHATSAPP', NULL, 
'¡Pago confirmado! Tu pago de *$${amount} ${currency}* para ${description} ha sido procesado exitosamente. ¡Gracias por confiar en Sigma Gym! 💳✅', 
'payment_success', true, NOW());

-- PAYMENT_FAILED templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('PAYMENT_FAILED', 'EMAIL', 'Error en el Pago - $${amount} ${currency}', 
'<h2>Error en el Pago</h2><p>No pudimos procesar tu pago:</p><ul><li><strong>Monto:</strong> $${amount} ${currency}</li><li><strong>Descripción:</strong> ${description}</li><li><strong>Error:</strong> ${errorMessage}</li></ul><p>Por favor, intenta nuevamente o contacta con nosotros.</p>', 
NULL, true, NOW()),

('PAYMENT_FAILED', 'PUSH', 'Error en el Pago', 
'No pudimos procesar tu pago de $${amount} ${currency}. Por favor, intenta nuevamente.', 
NULL, true, NOW()),

('PAYMENT_FAILED', 'WHATSAPP', NULL, 
'No pudimos procesar tu pago de *$${amount} ${currency}* para ${description}. Error: ${errorMessage}. Por favor, intenta nuevamente. 💳❌', 
'payment_failed', true, NOW());

-- PAYMENT_DUE templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('PAYMENT_DUE', 'EMAIL', 'Recordatorio de Pago - ${membershipType}', 
'<h2>Recordatorio de Pago</h2><p>Tu membresía está próxima a vencer:</p><ul><li><strong>Membresía:</strong> ${membershipType}</li><li><strong>Monto:</strong> $${amount} ${currency}</li><li><strong>Fecha de vencimiento:</strong> ${dueDate}</li></ul><p>Renueva tu membresía para seguir disfrutando de Sigma Gym.</p>', 
NULL, true, NOW()),

('PAYMENT_DUE', 'PUSH', 'Recordatorio de Pago', 
'Tu membresía ${membershipType} vence el ${dueDate}. Renueva por $${amount} ${currency}.', 
NULL, true, NOW()),

('PAYMENT_DUE', 'WHATSAPP', NULL, 
'Tu membresía *${membershipType}* vence el ${dueDate}. Renueva por *$${amount} ${currency}* para seguir disfrutando de Sigma Gym. 💪📅', 
'payment_due', true, NOW());

-- WAITLIST_PROMOTED templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('WAITLIST_PROMOTED', 'EMAIL', '¡Tu turno ha llegado! - ${className}', 
'<h2>¡Has sido promovido de la lista de espera!</h2><p>¡Excelentes noticias ${firstName}! Un lugar se ha liberado en la clase que esperabas:</p><ul><li><strong>Clase:</strong> ${className} (${classType})</li><li><strong>Fecha:</strong> ${startsAt}</li><li><strong>Entrenador:</strong> ${trainerName}</li><li><strong>Posición anterior:</strong> #${position}</li></ul><p><strong>⏰ Tienes ${minutesToConfirm} minutos para confirmar tu lugar.</strong></p><p><a href="${confirmationUrl}" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Confirmar Lugar</a></p><p>Si no confirmas antes de las ${holdUntil}, el lugar será ofrecido al siguiente en la lista.</p>', 
NULL, true, NOW()),

('WAITLIST_PROMOTED', 'PUSH', '¡Tu turno ha llegado!', 
'¡${firstName}! Has sido promovido a ${className} el ${startsAt}. Tienes ${minutesToConfirm} minutos para confirmar tu lugar.', 
NULL, true, NOW()),

('WAITLIST_PROMOTED', 'WHATSAPP', NULL, 
'¡Excelentes noticias *${firstName}*! 🎉 Has sido promovido de la lista de espera para *${className}* (${classType}) el ${startsAt} con ${trainerName}. ⏰ Tienes *${minutesToConfirm} minutos* para confirmar tu lugar. Confirma aquí: ${confirmationUrl}', 
'waitlist_promoted', true, NOW());

-- WAITLIST_CONFIRMED templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('WAITLIST_CONFIRMED', 'EMAIL', 'Reserva Confirmada desde Lista de Espera - ${className}', 
'<h2>¡Reserva Confirmada!</h2><p>¡Perfecto ${firstName}! Has confirmado exitosamente tu lugar:</p><ul><li><strong>Clase:</strong> ${className}</li><li><strong>Fecha:</strong> ${startsAt}</li><li><strong>Entrenador:</strong> ${trainerName}</li><li><strong>ID de Reserva:</strong> #${bookingId}</li></ul><p>¡Felicitaciones por conseguir tu lugar desde la posición #${previousPosition} en la lista de espera!</p><p>¡Te esperamos en Sigma Gym!</p>', 
NULL, true, NOW()),

('WAITLIST_CONFIRMED', 'PUSH', 'Reserva Confirmada', 
'¡${firstName}! Has confirmado tu lugar en ${className} el ${startsAt}. ¡Te esperamos!', 
NULL, true, NOW()),

('WAITLIST_CONFIRMED', 'WHATSAPP', NULL, 
'¡Perfecto *${firstName}*! ✅ Has confirmado exitosamente tu lugar en *${className}* el ${startsAt} con ${trainerName}. ID de reserva: #${bookingId}. ¡Te esperamos en Sigma Gym! 💪', 
'waitlist_confirmed', true, NOW());

-- WAITLIST_HOLD_EXPIRED templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('WAITLIST_HOLD_EXPIRED', 'EMAIL', 'Tiempo de Confirmación Expirado - ${className}', 
'<h2>Tiempo de Confirmación Expirado</h2><p>Hola ${firstName},</p><p>Lamentablemente, tu tiempo para confirmar el lugar en la siguiente clase ha expirado:</p><ul><li><strong>Clase:</strong> ${className}</li><li><strong>Fecha:</strong> ${startsAt}</li></ul><p>El lugar ha sido ofrecido al siguiente participante en la lista de espera.</p><p>¡No te desanimes! Puedes unirte nuevamente a la lista de espera para futuras clases.</p>', 
NULL, true, NOW()),

('WAITLIST_HOLD_EXPIRED', 'PUSH', 'Tiempo de Confirmación Expirado', 
'${firstName}, tu tiempo para confirmar el lugar en ${className} ha expirado. El lugar fue ofrecido al siguiente en la lista.', 
NULL, true, NOW()),

('WAITLIST_HOLD_EXPIRED', 'WHATSAPP', NULL, 
'Hola *${firstName}* ⏰ Tu tiempo para confirmar el lugar en *${className}* el ${startsAt} ha expirado. El lugar ha sido ofrecido al siguiente en la lista de espera. ¡Puedes unirte nuevamente a futuras clases! 💪', 
'waitlist_hold_expired', true, NOW());

-- INVOICE_ISSUED templates
INSERT INTO notification_template (event_type, channel, subject, body, template_name, is_active, created_at) VALUES
('INVOICE_ISSUED', 'EMAIL', 'Comprobante de Pago - ${invoiceNumber}', 
'<h2>Comprobante de Pago</h2><p>Hola ${firstName},</p><p>Te adjuntamos el comprobante de tu pago:</p><ul><li><strong>Número:</strong> ${invoiceNumber}</li><li><strong>Fecha:</strong> ${issueDate}</li></ul><p>¡Gracias por confiar en Sigma Gym!</p><p><strong>Nota:</strong> Este comprobante también está disponible en tu panel de usuario.</p>', 
NULL, true, NOW()),

('INVOICE_ISSUED', 'PUSH', 'Comprobante Disponible', 
'${firstName}, tu comprobante ${invoiceNumber} ya está disponible. ¡Revisa tu email!', 
NULL, true, NOW()),

('INVOICE_ISSUED', 'WHATSAPP', NULL, 
'¡Hola *${firstName}*! 📄 Tu comprobante de pago *${invoiceNumber}* del ${issueDate} ya está disponible. Revisa tu email para descargarlo. ¡Gracias por confiar en Sigma Gym! 💪', 
'invoice_issued', true, NOW());
